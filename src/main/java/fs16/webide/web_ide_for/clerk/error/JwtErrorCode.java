package fs16.webide.web_ide_for.clerk.error;

import fs16.webide.web_ide_for.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.logging.LogLevel;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements ErrorCode {
	JWT_KID_FAILED("JWT-001", "JWT 토큰에서 키 식별자(kid)를 추출할 수 없습니다", LogLevel.ERROR),
	JWT_VALIDATION_FAILED("JWT-002", "JWT 토큰 검증에 실패했습니다", LogLevel.ERROR),
	PUBLIC_KEY_NOT_FOUND("JWT-003", "해당 kid에 대한 공개 키를 찾을 수 없습니다", LogLevel.ERROR),
	JWK_FETCH_FAILED("JWT-004","JWKS 공개 키 목록을 가져오는 데 실패했습니다",	LogLevel.ERROR);

	private final String code;
	private final String message;
	private final LogLevel logLevel;
}
