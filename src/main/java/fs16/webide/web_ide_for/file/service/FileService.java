package fs16.webide.web_ide_for.file.service;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.file.dto.FileCreateRequest;
import fs16.webide.web_ide_for.file.dto.FileCreateResponse;
import fs16.webide.web_ide_for.file.dto.FileMoveRequest;
import fs16.webide.web_ide_for.file.dto.FileMoveResponse;
import fs16.webide.web_ide_for.file.dto.FileRemoveRequest;
import fs16.webide.web_ide_for.file.dto.FileRemoveResponse;
import fs16.webide.web_ide_for.file.dto.FileTreeResponse;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequest;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponse;
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
    public FileCreateResponse createFile(FileCreateRequest requestDto) {
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
        return FileCreateResponse.builder()
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
    public FileCreateResponse createFileWithContent(FileCreateRequest requestDto) {
        // Validate that this is not a directory
        if (requestDto.getIsDirectory()) {
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }

        // Create the file
        FileCreateResponse responseDto = createFile(requestDto);

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
    public List<FileTreeResponse> getFileStructure(Long containerId) {
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
    private List<FileTreeResponse> buildFileTree(List<File> files, Map<Long, List<File>> filesByParentId) {
        return files.stream()
                .map(file -> {
                    // Build children list if this is a directory
                    List<FileTreeResponse> children = null;
                    if (file.getIsDirectory()) {
                        List<File> childFiles = filesByParentId.getOrDefault(file.getId(), new ArrayList<>());
                        children = buildFileTree(childFiles, filesByParentId);
                    }

                    // Create DTO for this file
                    return FileTreeResponse.builder()
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
    public FileUpdateResponse updateFile(FileUpdateRequest requestDto) {
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

        return FileUpdateResponse.builder()
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
    private String updatePath(File parent, String name) {
        // 1. 부모가 아예 없으면 최상위(Root) 파일이므로 /이름
        if (parent == null) {
            return "/" + name;
        }

        // 2. 부모의 path 추출
        String parentPath = parent.getPath();

        // 3. 부모의 path가 null인 경우 처리
        if (parentPath == null || parentPath.isEmpty()) {
            // 부모가 최상위(Root)에 있는 폴더인 경우, 경로는 /부모이름이 되어야 함
            parentPath = "/" + parent.getName();
        }

        // 4. 경로 조립 (중복 슬래시 방지)
        // parentPath가 "/"인 경우는 이미 3번에서 걸러지거나 root 폴더인 경우임
        if (parentPath.equals("/")) {
            return "/" + name;
        }

        return parentPath.endsWith("/") ? parentPath + name : parentPath + "/" + name;
    }

    /**
     * 파일 또는 폴더를 이동합니다.
     */
    @Transactional
    public FileMoveResponse moveFile(FileMoveRequest request) {

        // 1. 이동할 파일 조회
        File file = fileRepository.findById(request.getFileId())
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // 2. 대상 부모 디렉토리 결정
        File targetParent = null; // 기본값 null (루트 의미)

        // targetParentId가 있는 경우에만 DB에서 부모를 조회
        if (request.getTargetParentId() != null) {
            targetParent = fileRepository.findById(request.getTargetParentId())
                .orElseThrow(() -> new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND));

            // 폴더가 아닌 곳으로 이동 시도 시 예외
            if (!targetParent.getIsDirectory()) {
                throw new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND);
            }

            // 순환 참조 방지
            if (isChildOf(targetParent, file)) {
                throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
            }
        }

        // 3. 새로운 루트 경로 계산 (위에서 수정한 updatePath 사용)
        String newRootPath = updatePath(targetParent, file.getName());

        // 4. 재귀적으로 DB와 S3 경로 업데이트 실행
        moveRecursive(file, targetParent, newRootPath);

        return FileMoveResponse.builder()
            .fileId(file.getId())
            .fileName(file.getName())
            .newParentId(file.getParent() != null ? file.getParent().getId() : null)
            .newPath(file.getPath())
            .isDirectory(file.getIsDirectory())
            .updatedAt(file.getUpdatedAt())
            .description("파일이 루트 또는 지정된 폴더로 성공적으로 이동되었습니다.")
            .build();
    }

    /**
     * 파일 및 폴더를 재귀적으로 이동시키고 DB/S3 정보를 갱신합니다.
     */
    private void moveRecursive(File file, File newParent, String newPath) {
        // 중요: S3 이동 시 이제 엔티티 객체를 직접 넘깁니다.
        s3FileService.moveS3Object(file, newPath);

        // DB 정보 업데이트
        file.setParent(newParent);
        file.setPath(newPath);
        fileRepository.save(file);

        // 폴더인 경우 하위 자식들도 재귀적으로 이동
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

    /**
     * 파일 또는 폴더를 삭제합니다. (하위 항목 포함)
     */
    @Transactional
    public FileRemoveResponse removeFile(FileRemoveRequest request) {
        // 1. 삭제할 파일 존재 확인
        File file = fileRepository.findById(request.getFileId())
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // 2. 컨테이너 소유권 검증 (보안 강화)
        if (!file.getContainerId().equals(request.getContainerId())) {
            throw new CoreException(FileErrorCode.CONTAINER_NOT_FOUND);
        }

        String deletedName = file.getName();
        String deletedPath = file.getPath();

        // 3. 재귀적으로 S3 및 DB 삭제 실행
        // Note: DB는 CascadeType.ALL 설정 덕분에 부모 삭제 시 자식도 삭제되지만,
        // S3 객체는 직접 하나하나 지워줘야 합니다.
        deleteRecursive(file);

        return FileRemoveResponse.builder()
            .fileId(request.getFileId())
            .fileName(deletedName)
            .filePath(deletedPath)
            .description("파일 및 하위 항목이 모두 삭제되었습니다.")
            .build();
    }

    /**
     * 하위 항목들을 탐색하며 S3 객체를 먼저 지우고 DB 레코드를 삭제합니다.
     */
    private void deleteRecursive(File file) {
        // 폴더인 경우 하위 자식들을 먼저 처리
        if (file.getIsDirectory()) {
            // findByParent를 사용하여 자식 목록 조회
            List<File> children = fileRepository.findByParent(file);
            for (File child : children) {
                deleteRecursive(child);
            }
        }

        // S3에서 삭제
        s3FileService.deleteFileFromS3(file);

        // DB에서 삭제 (부모부터 지우면 cascade에 의해 자식이 사라질 수 있으나,
        // S3 삭제를 위해 자식부터 명시적으로 지우는 것이 안전함)
        fileRepository.delete(file);
    }

}
