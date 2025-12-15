package fs16.webide.web_ide_for.common.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ErrorMessage {
	private final String code;
	private final String message;

	public static ErrorMessage from(ErrorCode errorCode) {
		return ErrorMessage.builder()
			.code(errorCode.getCode())
			.message(errorCode.getMessage())
			.build();
	}
}
