package fs16.webide.web_ide_for.file.service;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.file.dto.FileCreateRequestDto;
import fs16.webide.web_ide_for.file.dto.FileCreateResponseDto;
import fs16.webide.web_ide_for.file.dto.FileMoveRequest;
import fs16.webide.web_ide_for.file.dto.FileMoveResponse;
import fs16.webide.web_ide_for.file.dto.FileTreeResponseDto;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequestDto;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponseDto;
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

    @Transactional
    public FileUpdateResponseDto updateFile(FileUpdateRequestDto requestDto) {
        // 1. 파일 존재 확인
        File file = fileRepository.findById(requestDto.getFileId())
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        String oldPath = file.getPath();
        boolean isNameChanged = requestDto.getNewName() != null && !requestDto.getNewName().equals(file.getName());

        // 2. 파일 이름 및 경로 수정
        if (isNameChanged) {
            file.setName(requestDto.getNewName());
            // 부모 경로를 유지하면서 이름만 변경하여 새로운 path 생성
            String newPath = updatePath(file.getParent(), requestDto.getNewName());
            file.setPath(newPath);

            // 확장자 추출 로직 (필요 시)
            if (!file.getIsDirectory() && requestDto.getNewName().contains(".")) {
                file.setExtension(requestDto.getNewName().substring(requestDto.getNewName().lastIndexOf(".") + 1));
            }

            // S3 이름 변경 적용
            s3FileService.renameFileInS3(oldPath, file);
        }

        // 3. 파일 내용 수정 (디렉토리가 아닐 때만)
        if (!file.getIsDirectory() && requestDto.getNewContent() != null) {
            s3FileService.updateFileContentInS3(file, requestDto.getNewContent());
        }

        // DB 반영
        File updatedFile = fileRepository.save(file);

        return FileUpdateResponseDto.builder()
            .fileId(updatedFile.getId())
            .fileName(updatedFile.getName())
            .parentId(updatedFile.getParent() != null ? updatedFile.getParent().getId() : null)
            .isDirectory(updatedFile.getIsDirectory())
            .filePath(updatedFile.getPath())
            .createdAt(updatedFile.getCreatedAt())
            .updatedAt(updatedFile.getUpdatedAt())
            .fileExtension(updatedFile.getExtension())
            .content(requestDto.getNewContent())
            .description("파일 정보가 수정되었습니다.")
            .build();
    }

    /**
     * 부모 디렉토리 정보와 새 이름을 조합하여 새로운 경로를 생성합니다.
     */
    private String updatePath(File parent, String newName) {
        if (parent == null) {
            return "/" + newName;
        }
        String parentPath = parent.getPath();
        return parentPath.endsWith("/") ? parentPath + newName : parentPath + "/" + newName;
    }

    /**
     * 파일 또는 폴더를 이동합니다.
     */
    @Transactional
    public FileMoveResponse moveFile(FileMoveRequest request) {

        // 1. fileId 자체가 null인지 먼저 확인
        if (request.getFileId() == null) {
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH); // 혹은 적절한 에러 코드
        }

        // 1. 이동할 파일 존재 확인
        File file = fileRepository.findById(request.getFileId())
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // 2. 대상 부모 디렉토리 확인 (루트로 이동하는 경우 null일 수 있음)
        File targetParent = null;

        if (request.getTargetParentId() != null) {
            targetParent = fileRepository.findById(request.getTargetParentId())
                .orElseThrow(() -> new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND));

            if (!targetParent.getIsDirectory()) {
                throw new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND);
            }

            // 순환 참조 방지 (자기 자신이나 자신의 하위 폴더로 이동 불가)
            if (isChildOf(targetParent, file)) {
                throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
            }
        }

        // 4. 경로 업데이트 및 이동 로직 실행...
        String newPath = updatePath(targetParent, file.getName());
        moveRecursive(file, targetParent, newPath);

        return FileMoveResponse.builder()
            .fileId(file.getId())
            .fileName(file.getName())
            .newParentId(file.getParent() != null ? file.getParent().getId() : null)
            .newPath(file.getPath())
            .isDirectory(file.getIsDirectory())
            .updatedAt(file.getUpdatedAt())
            .description("파일 이동이 완료되었습니다.")
            .build();
    }

    /**
     * 파일 및 폴더를 재귀적으로 이동시키고 DB/S3 정보를 갱신합니다.
     */
    private void moveRecursive(File file, File newParent, String newPath) {
        String oldPath = file.getPath();

        // S3 이동 실행
        s3FileService.moveS3Object(oldPath, newPath, file.getContainerId());

        // DB 정보 업데이트
        file.setParent(newParent);
        file.setPath(newPath);
        fileRepository.save(file);

        // 폴더인 경우 하위 자식들도 이동
        if (file.getIsDirectory()) {
            List<File> children = fileRepository.findByParent(file);
            for (File child : children) {
                String childNewPath = newPath.endsWith("/") ? newPath + child.getName() : newPath + "/" + child.getName();
                moveRecursive(child, file, childNewPath);
            }
        }
    }

    /**
     * target이 potentialParent의 하위 폴더인지 확인 (순환 참조 체크)
     */
    private boolean isChildOf(File node, File potentialParent) {
        if (node == null) return false;
        if (node.getId().equals(potentialParent.getId())) return true;
        return isChildOf(node.getParent(), potentialParent);
    }

}
