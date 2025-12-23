package fs16.webide.web_ide_for.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.file.entity.ContainerFile;
import fs16.webide.web_ide_for.file.error.FileErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * Creates a file in S3 based on the File entity
     * @param containerFile The File entity
     * @param content The content of the file
     */
    public void createFileInS3(ContainerFile containerFile, String content) {
        try {
            String s3Key = generateS3Key(containerFile);
            
            // Create metadata
            ObjectMetadata metadata = new ObjectMetadata();
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            metadata.setContentLength(contentBytes.length);
            
            // Set content type based on file extension
            String contentType = determineContentType(containerFile.getName());
            metadata.setContentType(contentType);
            
            // Upload file to S3
            InputStream inputStream = new ByteArrayInputStream(contentBytes);
            amazonS3Client.putObject(new PutObjectRequest(bucket, s3Key, inputStream, metadata));
            
            log.info("File created in S3: {}", s3Key);
        } catch (Exception e) {
            log.error("Error creating file in S3", e);
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }
    }

    /**
     * Creates a directory in S3 based on the File entity
     * @param containerFile The File entity
     */
    public void createDirectoryInS3(ContainerFile containerFile) {
        try {
            String s3Key = generateS3Key(containerFile);
            
            // Ensure the key ends with a slash to represent a directory
            if (!s3Key.endsWith("/")) {
                s3Key += "/";
            }
            
            // Create an empty object with the directory key
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(0);
            
            // Upload empty content to create directory marker
            InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
            amazonS3Client.putObject(new PutObjectRequest(bucket, s3Key, emptyContent, metadata));
            
            log.info("Directory created in S3: {}", s3Key);
        } catch (Exception e) {
            log.error("Error creating directory in S3", e);
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }
    }

    /**
     * Generates an S3 key based on the File entity
     * @param containerFile The File entity
     * @return The S3 key
     */
    private String generateS3Key(ContainerFile containerFile) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(containerFile.getContainerId()).append("/");

        if (containerFile.getPath() != null && !containerFile.getPath().isEmpty()) {
            String path = containerFile.getPath();
            // 앞쪽 슬래시 제거
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            keyBuilder.append(path);

            // 디렉토리이고 마지막에 슬래시가 없다면 추가 (S3 폴더 관례)
            if (containerFile.getIsDirectory() && !path.endsWith("/")) {
                keyBuilder.append("/");
            }
        } else {
            keyBuilder.append(containerFile.getName());
            if (containerFile.getIsDirectory()) {
                keyBuilder.append("/");
            }
        }

        return keyBuilder.toString();
    }

    /**
     * S3에서 파일의 내용을 읽어옵니다.
     */
    public String getFileContentFromS3(ContainerFile containerFile) {
        try {
            String s3Key = generateS3Key(containerFile); // 기존 키 생성 로직 활용

            // S3 객체 가져오기
            S3Object s3Object = amazonS3Client.getObject(bucket, s3Key);

            // InputStream을 String으로 변환 (UTF-8)

            return IOUtils.toString(s3Object.getObjectContent());
        } catch (Exception e) {
            log.error("Error reading file content from S3: {}", containerFile.getPath(), e);
            throw new CoreException(FileErrorCode.FILE_NOT_FOUND); // 파일이 없거나 읽기 실패 시 예외
        }
    }

    /**
     * Determines the content type based on the file extension
     * @param fileName The file name
     * @return The content type
     */
    private String determineContentType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "application/octet-stream";
        }
        
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "json":
                return "application/json";
            case "txt":
                return "text/plain";
            case "xml":
                return "application/xml";
            case "pdf":
                return "application/pdf";
            case "png":
                return "image/png";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "gif":
                return "image/gif";
            case "svg":
                return "image/svg+xml";
            case "md":
                return "text/markdown";
            case "java":
                return "text/x-java-source";
            case "py":
                return "text/x-python";
            case "c":
                return "text/x-c";
            case "cpp":
                return "text/x-c++";
            case "h":
                return "text/x-c";
            case "hpp":
                return "text/x-c++";
            case "ts":
                return "application/typescript";
            case "jsx":
                return "text/jsx";
            case "tsx":
                return "text/tsx";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * S3에 있는 파일의 내용을 업데이트합니다.
     */
    public void updateFileContentInS3(ContainerFile containerFile, String content) {
        createFileInS3(containerFile, content); // 기존 createFileInS3 로직이 PutObject이므로 덮어쓰기가 됩니다.
    }

    /**
     * S3에서 파일의 경로(이름)를 변경합니다.
     * @param oldPath 이전 S3 키
     * @param newContainerFile 새 정보가 반영된 File 엔티티
     */
    public void renameFileInS3(String oldPath, ContainerFile newContainerFile) {
        try {
            String oldS3Key = newContainerFile.getContainerId() + (oldPath.startsWith("/") ? oldPath : "/" + oldPath);
            String newS3Key = generateS3Key(newContainerFile);

            if (newContainerFile.getIsDirectory() && !newS3Key.endsWith("/")) {
                newS3Key += "/";
            }

            // 1. 기존 객체를 새 키로 복사
            amazonS3Client.copyObject(bucket, oldS3Key, bucket, newS3Key);
            // 2. 기존 객체 삭제
            amazonS3Client.deleteObject(bucket, oldS3Key);

            log.info("S3 file renamed from {} to {}", oldS3Key, newS3Key);
        } catch (Exception e) {
            log.error("Error renaming file in S3", e);
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }
    }

    /**
     * S3 내에서 객체의 위치를 이동(복사 후 삭제)합니다.
     * @param containerFile 이동할 파일
     * @param newPath 이동할 새 경로 (새로 계산된 path 값)
     */
    public void moveS3Object(ContainerFile containerFile, String newPath) {
        // 이동 전의 원래 경로를 로그용으로 보관
        String originalPath = containerFile.getPath();
        try {
            // 기존 파일 엔티티의 정보를 활용해 정확한 원본 S3 Key 생성
            String oldS3Key = generateS3Key(containerFile);
            String newS3Key = formatS3Key(containerFile.getContainerId(), newPath);

            log.info("S3 MOVE ATTEMPT: [Source: {}] -> [Target: {}]", oldS3Key, newS3Key);

            // 원본 파일 존재 여부 확인 (NoSuchKey 방지)
            if (!amazonS3Client.doesObjectExist(bucket, oldS3Key)) {
                log.error("SOURCE KEY NOT FOUND IN S3: {}", oldS3Key);
                throw new CoreException(FileErrorCode.FILE_NOT_FOUND);
            }

            // 1. 기존 객체를 새 위치로 복사
            amazonS3Client.copyObject(new CopyObjectRequest(bucket, oldS3Key, bucket, newS3Key));

            // 2. 복사가 완료된 후 기존 위치의 객체 삭제
            amazonS3Client.deleteObject(bucket, oldS3Key);

            log.info("S3 Object moved successfully: {} -> {}", oldS3Key, newS3Key);
        } catch (Exception e) {
            // oldPath 대신 originalPath 또는 file.getPath() 사용
            log.error("Failed to move S3 object for file ID: {}, NewPath: {}", containerFile.getId(), newPath, e);
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }
    }

    private String buildFullS3Key(Long containerId, String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return containerId.toString() + "/";
        }

        // DB path가 "/"로 시작하면 제거하여 containerId/path 형태 유지
        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        return containerId + "/" + cleanPath;
    }

    /**
     * DB의 path 정보를 S3 Key 형식에 맞게 포맷팅합니다.
     */
    private String formatS3Key(Long containerId, String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return containerId + "/";
        }

        // 1. 앞뒤 공백 제거 및 중복 슬래시 방지
        String cleanPath = path.trim();

        // 2. 시작하는 "/" 제거 (containerId 뒤에 직접 붙이기 위함)
        while (cleanPath.startsWith("/")) {
            cleanPath = cleanPath.substring(1);
        }

        // 3. 만약 파일이 아니라 '폴더'라면 S3 관례상 마지막에 "/"가 있어야 함
        // moveRecursive에서 넘겨주는 newPath가 폴더인지 파일인지 확인이 필요할 수 있음

        return containerId + "/" + cleanPath;
    }

    /**
     * S3에서 파일 또는 디렉토리 객체를 삭제합니다.
     */
    public void deleteFileFromS3(ContainerFile containerFile) {
        try {
            String s3Key = generateS3Key(containerFile); // 기존에 작성된 key 생성 로직 활용

            // 디렉토리인 경우 S3 관례상 끝에 /가 붙어야 함
            if (containerFile.getIsDirectory() && !s3Key.endsWith("/")) {
                s3Key += "/";
            }

            amazonS3Client.deleteObject(bucket, s3Key);
            log.info("S3 object deleted: {}", s3Key);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", containerFile.getPath(), e);
            throw new CoreException(FileErrorCode.INVALID_FILE_PATH);
        }
    }
}
