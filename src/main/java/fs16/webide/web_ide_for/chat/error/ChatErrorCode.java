package fs16.webide.web_ide_for.chat.error;

import fs16.webide.web_ide_for.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
	HEADER_NOT_FOUND("CHA-001", "STOMP 인증 헤더가 전달되지 않았습니다", LogLevel.ERROR);


	private final String code;
	private final String message;
	private final LogLevel logLevel;
}
