package fs16.webide.web_ide_for.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import fs16.webide.web_ide_for.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 전역 예외 처리 핸들러 (ApiResponse 표준 응답 형식 사용)
 * * @RestControllerAdvice를 사용하여 모든 Controller의 예외를 중앙에서 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. CoreException 처리: 사용자 정의 비즈니스 예외 처리
     * CoreException에 담긴 ErrorCode를 사용하여 ApiResponse 오류 응답을 생성합니다.
     */
    @ExceptionHandler(CoreException.class)
    public ApiResponse<Void> handleCoreException(CoreException ex ,HttpServletRequest request
    ) {
        String uri = request.getRequestURI();

        // Swagger 요청 제외
        if (uri.startsWith("/v3/api-docs")
                || uri.startsWith("/swagger-ui")) {
            throw ex;
        }
        ErrorCode errorCode = ex.getErrorCode();

        logByLevel(errorCode, ex); // 로그 레벨에 따른 기록 유지

        // HttpStatus는 이 응답(ApiResponse)을 받는 쪽에서 ErrorCode의 정보를 바탕으로 판단하거나,
        // ErrorCode 자체에 포함되어야 합니다. 여기서는 ApiResponse 객체만 반환합니다.
        return ApiResponse.error(ErrorMessage.from(errorCode));
    }

    /**
     * 2. Validation 예외 처리: Spring 유효성 검사 예외 처리 (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        // 첫 번째 필드 오류의 메시지를 추출하여 로그에 기록
        String defaultMessage = ex.getBindingResult().getFieldError() != null
            ? ex.getBindingResult().getFieldError().getDefaultMessage()
            : "Validation failed";

        log.warn("Validation failed: {}", defaultMessage);

        // BAD_REQUEST에 해당하는 공통 오류 코드를 사용하여 ApiResponse를 반환합니다.
        return ApiResponse.error(ErrorMessage.from(CommonErrorCode.BAD_REQUEST));
    }

    /**
     * 3. 최상위 일반 예외 처리: 예상치 못한 모든 예외 처리 (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);

        // 서버 오류에 해당하는 공통 오류 코드를 사용하여 ApiResponse를 반환합니다.
        return ApiResponse.error(ErrorMessage.from(CommonErrorCode.SERVER_ERROR));
    }

    /**
     * 4. 로그 레벨별 처리 로직 (Kotlin 코드의 로직을 Java로 구현)
     */
    private void logByLevel(ErrorCode errorCode, Exception ex) {
        String message = String.format("[%s] %s", errorCode.getCode(), ex.getMessage());

        // ErrorCode 인터페이스에 getLogLevel()이 정의되어 있다고 가정합니다.
        // LogLevel enum의 존재를 가정하고 switch-case를 사용합니다.
        switch (errorCode.getLogLevel()) {
            case ERROR -> log.error(message, ex);
            case WARN -> log.warn(message);
            case INFO -> log.info(message);
            // DEBUG는 Kotlin 코드에 없었으므로 제거했으나, 필요 시 추가 가능합니다.
            default -> log.error(message, ex); // 기본은 ERROR로 처리
        }
    }
}
