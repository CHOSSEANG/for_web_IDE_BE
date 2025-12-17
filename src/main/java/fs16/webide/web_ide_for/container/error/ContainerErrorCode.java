package fs16.webide.web_ide_for.container.error;

import org.springframework.boot.logging.LogLevel;

import fs16.webide.web_ide_for.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContainerErrorCode implements ErrorCode {
	CONTAINER_NOT_FOUND("CON-001", "해당 컨테이너를 찾을 수 없습니다.", LogLevel.WARN);


	private final String code;
	private final String message;
	private final LogLevel logLevel;
}
