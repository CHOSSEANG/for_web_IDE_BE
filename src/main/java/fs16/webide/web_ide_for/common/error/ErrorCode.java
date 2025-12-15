package fs16.webide.web_ide_for.common.error;

import org.springframework.boot.logging.LogLevel;

public interface ErrorCode {
	String getCode();
	String getMessage();
	LogLevel getLogLevel();
}
