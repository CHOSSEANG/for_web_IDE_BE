package fs16.webide.web_ide_for.file.error;

import org.springframework.boot.logging.LogLevel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import fs16.webide.web_ide_for.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FILE_NOT_FOUND("FILE-001", "File not found.", LogLevel.INFO),
    DIRECTORY_NOT_FOUND("FILE-002", "Directory not found.", LogLevel.INFO),
    INVALID_FILE_PATH("FILE-003", "Invalid file path.", LogLevel.WARN),
    FILE_ALREADY_EXISTS("FILE-004", "File already exists.", LogLevel.WARN),
    DIRECTORY_ALREADY_EXISTS("FILE-005", "Directory already exists.", LogLevel.WARN),
    CONTAINER_NOT_FOUND("FILE-006", "Container not found.", LogLevel.INFO),
    PERMISSION_DENIED("FILE-007", "Permission denied.", LogLevel.WARN);

    private final String code;
    private final String message;
    private final LogLevel logLevel;
}
