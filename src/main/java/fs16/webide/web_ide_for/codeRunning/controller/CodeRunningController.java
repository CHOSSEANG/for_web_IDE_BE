package fs16.webide.web_ide_for.codeRunning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.Response;

import fs16.webide.web_ide_for.codeRunning.dto.CodeRunRequest;
import fs16.webide.web_ide_for.codeRunning.dto.CodeRunResponse;
import fs16.webide.web_ide_for.codeRunning.service.CodeRunningService;
import fs16.webide.web_ide_for.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
public class CodeRunningController {

	private final CodeRunningService codeRunningService;

	@PostMapping("/run")
	public ResponseEntity<CodeRunResponse> runCode(@RequestBody CodeRunRequest request) {
		log.info("코드를 실행합니다: {}", request.getCode());

		try {
			// 아직은 테스트 단계이므로, 전달받은 코드가 '명령어'라고 가정하고 실행합니다.
			// 나중에는 이 'code'를 파일로 만들어 EC2에 올리는 로직이 필요합니다.
			String result = codeRunningService.executeCommand(request.getCode());

			return ResponseEntity.ok(CodeRunResponse.builder()
				.output(result)
				.success(true)
				.build());

		} catch (Exception e) {
			log.error("코드 실행 중 에러 발생: ", e);
			return ResponseEntity.ok(CodeRunResponse.builder()
					.output(null)
					.success(false)
					.errorMessage(e.getMessage())
					.build()
			);
		}
	}
}
