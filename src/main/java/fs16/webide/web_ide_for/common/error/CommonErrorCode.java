package fs16.webide.web_ide_for.common.error;

import org.springframework.boot.logging.LogLevel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode{
	SERVER_ERROR("SYS-001", "예상치 못한 오류가 발생했습니다..", LogLevel.ERROR),
	BAD_REQUEST("SYS-002", "잘못된 요청입니다.", LogLevel.WARN),
	UNAUTHORIZED("SYS-003", "인증되지 않은 접근입니다.", LogLevel.WARN),
	FORBIDDEN("SYS-004", "접근 권한이 없습니다.", LogLevel.WARN),
	NOT_FOUND("SYS-005", "요청하신 리소스를 찾을 수 없습니다.", LogLevel.INFO);

	private final String code;
	private final String message;
	private final LogLevel logLevel;
}
