package fs16.webide.web_ide_for.file.service;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.file.dto.FileCreateRequestDto;
import fs16.webide.web_ide_for.file.dto.FileCreateResponseDto;
import fs16.webide.web_ide_for.file.dto.FileTreeResponseDto;
import fs16.webide.web_ide_for.file.entity.File;
import fs16.webide.web_ide_for.file.error.FileErrorCode;
import fs16.webide.web_ide_for.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final ContainerRepository containerRepository;
    private final S3FileService s3FileService;

    /**
     * Creates a file or directory
     * @param requestDto The file creation request
     * @return The created file response
     */
    @Transactional
    public FileCreateResponseDto createFile(FileCreateRequestDto requestDto) {
        // Validate container exists
        Container container = containerRepository.findById(requestDto.getContainerId())
                .orElseThrow(() -> new CoreException(FileErrorCode.CONTAINER_NOT_FOUND));

        // Validate parent directory exists if parentId is provided
        if (requestDto.getParentId() != null) {
            Optional<File> parentDirectory = fileRepository.findById(requestDto.getParentId());
            if (parentDirectory.isEmpty() || !parentDirectory.get().getIsDirectory()) {
                throw new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND);
            }
        }

        // Check if file already exists
        String path = requestDto.getFilePath();
        if (path != null && !path.isEmpty()) {
            Optional<File> existingFile = fileRepository.findByContainerIdAndPath(
                    requestDto.getContainerId(), path);
            if (existingFile.isPresent()) {
                if (requestDto.getIsDirectory()) {
                    throw new CoreException(FileErrorCode.DIRECTORY_ALREADY_EXISTS);
                } else {
                    throw new CoreException(FileErrorCode.FILE_ALREADY_EXISTS);
                }
            }
        }

        // Create file entity
        File file = new File();
        file.setContainerId(requestDto.getContainerId());
        file.setName(requestDto.getName());

        // Set parent if parentId is provided
        if (requestDto.getParentId() != null) {
            File parentFile = fileRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND));
            file.setParent(parentFile);
        }

        file.setIsDirectory(requestDto.getIsDirectory());
        file.setPath(requestDto.getFilePath());
        file.setExtension(requestDto.getFileExtension());

        // Save file to database
        File savedFile = fileRepository.save(file);

        // Create file in S3
        s3FileService.createFileInS3(savedFile);

        // Return response
        return FileCreateResponseDto.builder()
                .id(savedFile.getId())
                .containerId(savedFile.getContainerId())
                .fileName(savedFile.getName())
                .parentDirectoryId(savedFile.getParent() != null ? savedFile.getParent().getId() : null)
                .isDirectory(savedFile.getIsDirectory())
                .filePath(savedFile.getPath())
                .createdAt(savedFile.getCreatedAt())
                .updatedAt(savedFile.getUpdatedAt())
                .fileExtension(savedFile.getExtension())
                .description("파일이 생성되었습니다.")
                .build();
    }

    /**
     * Creates a file with content
     * @param requestDto The file creation request with content
     * @return The created file response
     */
    @Transactional
    public FileCreateResponseDto createFileWithContent(FileCreateRequestDto requestDto) {
        // Validate that this is not a directory
        if (requestDto.getIsDirectory()) {
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }

        // Create the file
        FileCreateResponseDto responseDto = createFile(requestDto);

        // Get the created file
        File file = fileRepository.findById(responseDto.getId())
                .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // Update the file content in S3
        s3FileService.createFileInS3(file, requestDto.getContent());

        return responseDto;
    }

    /**
     * Retrieves the file structure of a container
     * @param containerId The ID of the container
     * @return A list of file structure DTOs representing the root files/directories
     */
    @Transactional(readOnly = true)
    public List<FileTreeResponseDto> getFileStructure(Long containerId) {
        // Validate container exists
        containerRepository.findById(containerId)
                .orElseThrow(() -> new CoreException(FileErrorCode.CONTAINER_NOT_FOUND));

        // Get all files in the container
        List<File> allFiles = fileRepository.findByContainerId(containerId);

        // Group files by parent ID
        Map<Long, List<File>> filesByParentId = allFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getParent() != null ? file.getParent().getId() : 0L
                ));

        // Get root files (files with no parent or parent outside this container)
        List<File> rootFiles = filesByParentId.getOrDefault(0L, new ArrayList<>());

        // Build the file tree starting from root files
        return buildFileTree(rootFiles, filesByParentId);
    }

    /**
     * Recursively builds a file tree structure
     * @param files The files at the current level
     * @param filesByParentId Map of files grouped by parent ID
     * @return A list of file structure DTOs
     */
    private List<FileTreeResponseDto> buildFileTree(List<File> files, Map<Long, List<File>> filesByParentId) {
        return files.stream()
                .map(file -> {
                    // Build children list if this is a directory
                    List<FileTreeResponseDto> children = null;
                    if (file.getIsDirectory()) {
                        List<File> childFiles = filesByParentId.getOrDefault(file.getId(), new ArrayList<>());
                        children = buildFileTree(childFiles, filesByParentId);
                    }

                    // Create DTO for this file
                    return FileTreeResponseDto.builder()
                            .id(file.getId())
                            .name(file.getName())
                            .path(file.getPath())
                            .isDirectory(file.getIsDirectory())
                            .extension(file.getExtension())
                            .createdAt(file.getCreatedAt())
                            .updatedAt(file.getUpdatedAt())
                            .children(children)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
