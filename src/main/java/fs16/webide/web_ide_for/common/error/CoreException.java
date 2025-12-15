package fs16.webide.web_ide_for.common.error;

import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
	private final ErrorCode errorCode;

	public CoreException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public CoreException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public CoreException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}
}
