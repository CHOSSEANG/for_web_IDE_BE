package fs16.webide.web_ide_for.file.error;

import org.springframework.boot.logging.LogLevel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import fs16.webide.web_ide_for.common.error.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FILE_NOT_FOUND("FILE-001", "해당 파일을 찾을 수 없습니다.", LogLevel.INFO),
    DIRECTORY_NOT_FOUND("FILE-002", "해당 폴더를 찾을 수 없습니다.", LogLevel.INFO),
    INVALID_FILE_PATH("FILE-003", "해당 경로를 찾을 수 없습니다.", LogLevel.WARN),
    FILE_ALREADY_EXISTS("FILE-004", "중복된 파일이 있습니다.", LogLevel.WARN),
    DIRECTORY_ALREADY_EXISTS("FILE-005", "중복된 폴더가 있습니다.", LogLevel.WARN),
    PERMISSION_DENIED("FILE-006", "접근 권한이 없습니다.", LogLevel.WARN),
    DIRECTORY_CANNOT_INCLUDE_CONTENT("FILE-007","폴더에는 내용을 추가할 수 없습니다.", LogLevel.WARN);

    private final String code;
    private final String message;
    private final LogLevel logLevel;
}
