package fs16.webide.web_ide_for.codeRunning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fs16.webide.web_ide_for.codeRunning.dto.CodeCommandRequest;
import fs16.webide.web_ide_for.codeRunning.dto.CodeCommandResponse;
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

	@PostMapping("/command")
	public ResponseEntity<CodeCommandResponse> runCode(@RequestBody CodeCommandRequest request) {
		log.info("코드를 실행합니다: {}", request.getCode());

		try {
			// 아직은 테스트 단계이므로, 전달받은 코드가 '명령어'라고 가정하고 실행합니다.
			// 나중에는 이 'code'를 파일로 만들어 EC2에 올리는 로직이 필요합니다.
			String result = codeRunningService.executeCommand(request.getCode());

			return ResponseEntity.ok(CodeCommandResponse.builder()
				.output(result)
				.success(true)
				.build());

		} catch (Exception e) {
			log.error("코드 실행 중 에러 발생: ", e);
			return ResponseEntity.ok(CodeCommandResponse.builder()
					.output(null)
					.success(false)
					.errorMessage(e.getMessage())
					.build()
			);
		}
	}

	// 신규 통합 실행 로직 (S3 -> EC2 -> Run -> Clean)
	@GetMapping("/{userId}/{containerId}/{fileId}/run")
	public ApiResponse<CodeRunResponse> runFile(@PathVariable Long userId, @PathVariable Long containerId, @PathVariable Long fileId) {
		String result = codeRunningService.runS3FileOnEc2(userId, containerId, fileId);

		return ApiResponse.success(CodeRunResponse.builder()
			.fileId(fileId)
			.result(result)
			.success(!result.contains("[ERROR]"))
			.build());
	}
}
