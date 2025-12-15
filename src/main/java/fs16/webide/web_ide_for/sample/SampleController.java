package fs16.webide.web_ide_for.sample;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.common.error.CommonErrorCode;
import fs16.webide.web_ide_for.common.error.CoreException;

/**
 * Sample controller to demonstrate error handling with ApiResponse.
 */
@RestController
@RequestMapping("/api/sample")
public class SampleController {

    /**
     * Returns a simple success message, wrapped in ApiResponse.
     * @return ApiResponse<String> 성공 응답
     */
    @GetMapping
    public ApiResponse<String> getSample() {
        // ResponseEntity 대신 ApiResponse.success() 사용
        return ApiResponse.success("Sample API is working!");
    }

    /**
     * Demonstrates different error scenarios based on the provided error type.
     * * @param errorType The type of error to simulate (server-error, bad-request, etc.)
     * @return Never returns normally as it always throws an exception
     */
    @GetMapping("/error/{errorType}")
    public ApiResponse<String> getError(@PathVariable String errorType) {
        // 반환 타입은 ApiResponse<String>이지만, 실제로 예외가 발생하여 핸들러로 넘어갑니다.
        switch (errorType) {
            case "server-error":
                throw new CoreException(CommonErrorCode.SERVER_ERROR);
            case "bad-request":
                throw new CoreException(CommonErrorCode.BAD_REQUEST);
            case "not-found":
                throw new CoreException(CommonErrorCode.NOT_FOUND);
            case "unauthorized":
                throw new CoreException(CommonErrorCode.UNAUTHORIZED);
            case "forbidden":
                throw new CoreException(CommonErrorCode.FORBIDDEN);
                // 일반적인 Java Exception을 테스트하려면, 아래처럼 추가할 수 있습니다.
            case "generic":
                throw new RuntimeException("Simulated unexpected generic error.");
            default:
                throw new CoreException(CommonErrorCode.BAD_REQUEST, "Invalid error type: " + errorType);
        }
    }
}
