package fs16.webide.web_ide_for.file.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.error.ContainerErrorCode;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.file.dto.FileCreateRequest;
import fs16.webide.web_ide_for.file.dto.FileCreateResponse;
import fs16.webide.web_ide_for.file.dto.FileLoadResponse;
import fs16.webide.web_ide_for.file.dto.FileMoveRequest;
import fs16.webide.web_ide_for.file.dto.FileMoveResponse;
import fs16.webide.web_ide_for.file.dto.FileRemoveResponse;
import fs16.webide.web_ide_for.file.dto.FileTreeResponse;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequest;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponse;
import fs16.webide.web_ide_for.file.entity.ContainerFile;
import fs16.webide.web_ide_for.file.error.FileErrorCode;
import fs16.webide.web_ide_for.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

        // 1. 컨테이너 존재 검증
        Container container = containerRepository.findById(requestDto.getContainerId())
            .orElseThrow(() -> new CoreException(ContainerErrorCode.CONTAINER_NOT_FOUND));

        // 2. 부모 디렉토리 정보 조회 및 검증
        ContainerFile parentContainerFile = null;
        if (requestDto.getParentId() != null) {
            parentContainerFile = fileRepository.findById(requestDto.getParentId())
                .orElseThrow(() -> new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND));

            if (!parentContainerFile.getIsDirectory()) {
                throw new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND);
            }
        }

        // 3. 이름(name)을 분석하여 isDirectory와 extension 결정
        String name = requestDto.getName();
        boolean isDirectory = !name.contains(".");
        String extension = null;

        if (!isDirectory) {
            int lastDotIndex = name.lastIndexOf('.');
            if (lastDotIndex > 0) {
                extension = name.substring(lastDotIndex + 1);
            }
        } else if(requestDto.getContent() != null && !requestDto.getContent().isEmpty()) {
            throw new CoreException(FileErrorCode.DIRECTORY_CANNOT_INCLUDE_CONTENT);
        }

        // 4. 서버에서 정확한 전체 경로(Full Path) 생성
        String fullPath = updatePath(parentContainerFile, name);

        // 5. 중복 파일 체크
        Optional<ContainerFile> existingFile = fileRepository.findByContainerIdAndPath(
            requestDto.getContainerId(), fullPath);
        if (existingFile.isPresent()) {
            throw new CoreException(isDirectory ?
                FileErrorCode.DIRECTORY_ALREADY_EXISTS : FileErrorCode.FILE_ALREADY_EXISTS);
        }

        // 6. 엔티티 생성 및 정보 설정
        ContainerFile containerFile = new ContainerFile();
        containerFile.setContainerId(requestDto.getContainerId());
        containerFile.setName(name);
        containerFile.setParent(parentContainerFile);
        containerFile.setIsDirectory(isDirectory);
        containerFile.setPath(fullPath);
        containerFile.setExtension(extension);

        // 7. DB 저장
        ContainerFile savedContainerFile = fileRepository.save(containerFile);

        // 8. S3 생성 (통합 로직)
        if (isDirectory) {
            // 디렉토리인데 내용이 포함된 경우 예외 처리
            if (requestDto.getContent() != null && !requestDto.getContent().isEmpty()) {
                throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
            }
            s3FileService.createDirectoryInS3(savedContainerFile);
        } else {
            // 파일인 경우: 내용이 있으면 내용 포함, 없으면 빈 파일 생성
            String content = (requestDto.getContent() != null) ? requestDto.getContent() : "";
            s3FileService.createFileInS3(savedContainerFile, content);
        }

        return FileCreateResponse.builder()
            .id(savedContainerFile.getId())
            .containerId(savedContainerFile.getContainerId())
            .fileName(savedContainerFile.getName())
            .parentDirectoryId(savedContainerFile.getParent() != null ? savedContainerFile.getParent().getId() : null)
            .isDirectory(savedContainerFile.getIsDirectory())
            .filePath(savedContainerFile.getPath())
            .createdAt(savedContainerFile.getCreatedAt())
            .updatedAt(savedContainerFile.getUpdatedAt())
            .fileExtension(savedContainerFile.getExtension())
            .description("파일/디렉토리가 성공적으로 생성되었습니다.")
            .build();
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
                .orElseThrow(() -> new CoreException(ContainerErrorCode.CONTAINER_NOT_FOUND));

        // Get all files in the container
        List<ContainerFile> allContainerFiles = fileRepository.findByContainerId(containerId);

        // Group files by parent ID
        Map<Long, List<ContainerFile>> filesByParentId = allContainerFiles.stream()
                .collect(Collectors.groupingBy(
                        containerFile -> containerFile.getParent() != null ? containerFile.getParent().getId() : 0L
                ));

        // Get root files (files with no parent or parent outside this container)
        List<ContainerFile> rootContainerFiles = filesByParentId.getOrDefault(0L, new ArrayList<>());

        // Build the file tree starting from root files
        return buildFileTree(rootContainerFiles, filesByParentId);
    }

    /**
     * 특정 파일의 내용을 조회합니다.
     */
    @Transactional(readOnly = true)
    public FileLoadResponse getFileContent(Long fileId) {
        // 1. DB에서 파일 존재 확인
        ContainerFile containerFile = fileRepository.findById(fileId)
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // 2. 디렉토리인지 확인 (디렉토리는 내용이 없음)
        if (containerFile.getIsDirectory()) {
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }

        // 3. S3에서 실제 텍스트 내용 읽기
        String content = s3FileService.getFileContentFromS3(containerFile);

        // 4. DTO 구성 및 반환
        return FileLoadResponse.builder()
            .fileId(containerFile.getId())
            .fileName(containerFile.getName())
            .filePath(containerFile.getPath())
            .content(content)
            .extension(containerFile.getExtension())
            .updatedAt(containerFile.getUpdatedAt())
            .description("파일 내용을 성공적으로 불러왔습니다.")
            .build();
    }

    /**
     * Recursively builds a file tree structure
     * @param containerFiles The files at the current level
     * @param filesByParentId Map of files grouped by parent ID
     * @return A list of file structure DTOs
     */
    private List<FileTreeResponse> buildFileTree(List<ContainerFile> containerFiles, Map<Long, List<ContainerFile>> filesByParentId) {
        return containerFiles.stream()
                .map(containerFile -> {
                    // Build children list if this is a directory
                    List<FileTreeResponse> children = null;
                    if (containerFile.getIsDirectory()) {
                        List<ContainerFile> childContainerFiles = filesByParentId.getOrDefault(containerFile.getId(), new ArrayList<>());
                        children = buildFileTree(childContainerFiles, filesByParentId);
                    }

                    // Create DTO for this file
                    return FileTreeResponse.builder()
                            .id(containerFile.getId())
                            .name(containerFile.getName())
                            .path(containerFile.getPath())
                            .isDirectory(containerFile.getIsDirectory())
                            .extension(containerFile.getExtension())
                            .createdAt(containerFile.getCreatedAt())
                            .updatedAt(containerFile.getUpdatedAt())
                            .children(children)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public FileUpdateResponse updateFile(Long fileId, FileUpdateRequest requestDto) {
        // 1. 파일 존재 확인
        ContainerFile containerFile = fileRepository.findById(fileId)
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        String oldPath = containerFile.getPath();
        boolean isNameChanged = requestDto.getNewName() != null && !requestDto.getNewName().equals(containerFile.getName());
        // 2. 파일 이름 및 경로 수정
        if (requestDto.getNewName() != null) {
            String newName = requestDto.getNewName();

            containerFile.setName(newName);

            // 부모 경로를 유지하면서 이름만 변경하여 새로운 path 생성
            String newPath = updatePath(containerFile.getParent(), newName);
            containerFile.setPath(newPath);

            // 확장자 추출 로직 (필요 시)
            if (!containerFile.getIsDirectory() && newName.contains(".")) {
                containerFile.setExtension(
                        newName.substring(newName.lastIndexOf(".") + 1)
                );
            }
            // S3 이름 변경 적용
            s3FileService.renameFileInS3(oldPath, containerFile);
        }

        // 3. 파일 내용 수정 (디렉토리가 아닐 때만)
        if (!containerFile.getIsDirectory() && requestDto.getNewContent() != null) {
            s3FileService.updateFileContentInS3(containerFile, requestDto.getNewContent());
        }

        // DB 반영
        ContainerFile updatedContainerFile = fileRepository.save(containerFile);

        return FileUpdateResponse.builder()
            .fileId(updatedContainerFile.getId())
            .fileName(updatedContainerFile.getName())
            .parentId(updatedContainerFile.getParent() != null ? updatedContainerFile.getParent().getId() : null)
            .isDirectory(updatedContainerFile.getIsDirectory())
            .filePath(updatedContainerFile.getPath())
            .createdAt(updatedContainerFile.getCreatedAt())
            .updatedAt(updatedContainerFile.getUpdatedAt())
            .fileExtension(updatedContainerFile.getExtension())
            .content(requestDto.getNewContent())
            .description("파일 정보가 수정되었습니다.")
            .build();
    }

    /**
     * 부모 디렉토리 정보와 새 이름을 조합하여 새로운 경로를 생성합니다.
     */
    private String updatePath(ContainerFile parent, String name) {
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
    public FileMoveResponse moveFile(Long fileId, FileMoveRequest request) {

        // 1. 이동할 파일 조회
        ContainerFile containerFile = fileRepository.findById(fileId)
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // 2. 대상 부모 디렉토리 결정
        ContainerFile targetParent = null; // 기본값 null (루트 의미)

        // targetParentId가 있는 경우에만 DB에서 부모를 조회
        if (request.getTargetParentId() != null) {
            targetParent = fileRepository.findById(request.getTargetParentId())
                .orElseThrow(() -> new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND));

            // 폴더가 아닌 곳으로 이동 시도 시 예외
            if (!targetParent.getIsDirectory()) {
                throw new CoreException(FileErrorCode.DIRECTORY_NOT_FOUND);
            }

            // 순환 참조 방지
            if (isChildOf(targetParent, containerFile)) {
                throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
            }
        }

        // 3. 새로운 루트 경로 계산 (위에서 수정한 updatePath 사용)
        String newRootPath = updatePath(targetParent, containerFile.getName());

        // 4. 재귀적으로 DB와 S3 경로 업데이트 실행
        moveRecursive(containerFile, targetParent, newRootPath);

        return FileMoveResponse.builder()
            .fileId(containerFile.getId())
            .fileName(containerFile.getName())
            .newParentId(containerFile.getParent() != null ? containerFile.getParent().getId() : null)
            .newPath(containerFile.getPath())
            .isDirectory(containerFile.getIsDirectory())
            .updatedAt(containerFile.getUpdatedAt())
            .description("파일이 루트 또는 지정된 폴더로 성공적으로 이동되었습니다.")
            .build();
    }

    /**
     * 파일 및 폴더를 재귀적으로 이동시키고 DB/S3 정보를 갱신합니다.
     */
    private void moveRecursive(ContainerFile containerFile, ContainerFile newParent, String newPath) {
        // 중요: S3 이동 시 이제 엔티티 객체를 직접 넘깁니다.
        s3FileService.moveS3Object(containerFile, newPath);

        // DB 정보 업데이트
        containerFile.setParent(newParent);
        containerFile.setPath(newPath);
        fileRepository.save(containerFile);

        // 폴더인 경우 하위 자식들도 재귀적으로 이동
        if (containerFile.getIsDirectory()) {
            List<ContainerFile> children = fileRepository.findByParent(containerFile);
            for (ContainerFile child : children) {
                String childNewPath = newPath.endsWith("/") ? newPath + child.getName() : newPath + "/" + child.getName();
                moveRecursive(child, containerFile, childNewPath);
            }
        }
    }

    /**
     * target이 potentialParent의 하위 폴더인지 확인 (순환 참조 체크)
     */
    private boolean isChildOf(ContainerFile node, ContainerFile potentialParent) {
        if (node == null) return false;
        if (node.getId().equals(potentialParent.getId())) return true;
        return isChildOf(node.getParent(), potentialParent);
    }

    /**
     * 파일 또는 폴더를 삭제합니다.
     * @param fileId 삭제할 파일 ID
     * @param containerId 권한 확인을 위한 컨테이너 ID
     */
    @Transactional
    public FileRemoveResponse removeFile(Long fileId, Long containerId) {
        // 1. 삭제할 파일 조회
        ContainerFile containerFile = fileRepository.findById(fileId)
            .orElseThrow(() -> new CoreException(FileErrorCode.FILE_NOT_FOUND));

        // 2. 컨테이너 소유권 검증
        if (!containerFile.getContainerId().equals(containerId)) {
            throw new CoreException(ContainerErrorCode.CONTAINER_NOT_FOUND);
        }

        String deletedName = containerFile.getName();
        String deletedPath = containerFile.getPath();

        // 3. 재귀적으로 S3 및 DB 삭제 실행
        deleteRecursive(containerFile);

        return FileRemoveResponse.builder()
            .fileId(fileId)
            .fileName(deletedName)
            .filePath(deletedPath)
            .description("파일이 성공적으로 삭제되었습니다.")
            .build();
    }

    /**
     * 하위 항목들을 탐색하며 S3 객체를 먼저 지우고 DB 레코드를 삭제합니다.
     */
    private void deleteRecursive(ContainerFile containerFile) {
        // 폴더인 경우 하위 자식들을 먼저 처리
        if (containerFile.getIsDirectory()) {
            // findByParent를 사용하여 자식 목록 조회
            List<ContainerFile> children = fileRepository.findByParent(containerFile);
            for (ContainerFile child : children) {
                deleteRecursive(child);
            }
        }

        // S3에서 삭제
        s3FileService.deleteFileFromS3(containerFile);

        // DB에서 삭제 (부모부터 지우면 cascade에 의해 자식이 사라질 수 있으나,
        // S3 삭제를 위해 자식부터 명시적으로 지우는 것이 안전함)
        fileRepository.delete(containerFile);
    }

}
