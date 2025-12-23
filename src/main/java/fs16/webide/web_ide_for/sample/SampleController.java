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
@RequestMapping("/health")
public class SampleController {

    /**
     * Returns a simple success message, wrapped in ApiResponse.
     * @return ApiResponse<String> 성공 응답
     */
    @GetMapping
    public ResponseEntity<String> getSample() {
        // health check를 위한 200 상태값 Return용 api
        return ResponseEntity.ok("success");
    }
}
