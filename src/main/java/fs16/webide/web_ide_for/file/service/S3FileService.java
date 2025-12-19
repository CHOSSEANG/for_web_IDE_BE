package fs16.webide.web_ide_for.file.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.file.entity.File;
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
     * Creates a file or directory in S3 based on the File entity
     * @param file The File entity
     */
    public void createFileInS3(File file) {
        if (file.getIsDirectory()) {
            createDirectoryInS3(file);
        } else {
            createFileInS3(file, "");
        }
    }

    /**
     * Creates a file in S3 based on the File entity
     * @param file The File entity
     * @param content The content of the file
     */
    public void createFileInS3(File file, String content) {
        try {
            String s3Key = generateS3Key(file);
            
            // Create metadata
            ObjectMetadata metadata = new ObjectMetadata();
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            metadata.setContentLength(contentBytes.length);
            
            // Set content type based on file extension
            String contentType = determineContentType(file.getName());
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
     * @param file The File entity
     */
    public void createDirectoryInS3(File file) {
        try {
            String s3Key = generateS3Key(file);
            
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
     * @param file The File entity
     * @return The S3 key
     */
    private String generateS3Key(File file) {
        // Use containerId as the root folder to separate files by container
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(file.getContainerId()).append("/");
        
        // Append the file path if it exists
        if (file.getPath() != null && !file.getPath().isEmpty()) {
            // Remove leading slash if present
            String path = file.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            keyBuilder.append(path);
            
            // Add trailing slash if it's a directory and doesn't already end with one
            if (file.getIsDirectory() && !path.endsWith("/")) {
                keyBuilder.append("/");
            }
        } else {
            // If no path, just use the file name
            keyBuilder.append(file.getName());
        }
        
        return keyBuilder.toString();
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
    public void updateFileContentInS3(File file, String content) {
        createFileInS3(file, content); // 기존 createFileInS3 로직이 PutObject이므로 덮어쓰기가 됩니다.
    }

    /**
     * S3에서 파일의 경로(이름)를 변경합니다.
     * @param oldPath 이전 S3 키
     * @param newFile 새 정보가 반영된 File 엔티티
     */
    public void renameFileInS3(String oldPath, File newFile) {
        try {
            String oldS3Key = newFile.getContainerId() + (oldPath.startsWith("/") ? oldPath : "/" + oldPath);
            String newS3Key = generateS3Key(newFile);

            if (newFile.getIsDirectory() && !newS3Key.endsWith("/")) {
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
}
