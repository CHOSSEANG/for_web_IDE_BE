package fs16.webide.web_ide_for.user.error;

import fs16.webide.web_ide_for.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND("USR-001", "해당 유저를 찾을 수 없습니다.", LogLevel.WARN),
    EMAIL_NOT_VERIFIED("USR-002", "인증되지 않은 이메일입니다.", LogLevel.WARN),
    USER_EXISTED("USR-003", "이미 초대된 유저입니다.", LogLevel.WARN);

    private final String code;
    private final String message;
    private final LogLevel logLevel;


}
