package fs16.webide.web_ide_for.common.error;

import org.springframework.boot.logging.LogLevel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{
	SERVER_ERROR("SYS-001", "An unexpected error has occurred.", LogLevel.ERROR),
	BAD_REQUEST("SYS-002", "Invalid request.", LogLevel.WARN),
	UNAUTHORIZED("SYS-003", "Unauthorized access.", LogLevel.WARN),
	FORBIDDEN("SYS-004", "Access denied.", LogLevel.WARN),
	NOT_FOUND("SYS-005", "Resource not found.", LogLevel.INFO);

	private final String code;
	private final String message;
	private final LogLevel logLevel;
}
