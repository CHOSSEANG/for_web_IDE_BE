package fs16.webide.web_ide_for.codeRunning.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
@Tag(name = "코드 실행 API", description = "EC2 원격 서버에서 코드를 실행하고 결과를 반환하는 API입니다.")
public class CodeRunningController {

	private final CodeRunningService codeRunningService;

	@Operation(summary = "단순 쉘 명령어 실행", description = "EC2 서버에 직접적인 쉘 명령어를 전달하고 결과를 출력합니다.")
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
	@Operation(summary = "파일 기반 통합 실행 로직", description = "S3의 파일을 EC2로 가져와 언어별(Java, Python, JS) 환경에서 실행한 뒤 결과를 반환합니다.")
	@GetMapping("/{userId}/{containerId}/{fileId}/run")
	public ApiResponse<CodeRunResponse> runFile(
		@Parameter(description = "사용자 고유 ID", example = "1")
		@AuthenticationPrincipal Long userId,
		@Parameter(description = "컨테이너 고유 ID", example = "15")
		@PathVariable Long containerId,
		@Parameter(description = "실행할 파일의 고유 ID (DB PK)", example = "30")
		@PathVariable Long fileId) {
		String result = codeRunningService.runS3FileOnEc2(userId, containerId, fileId);

		return ApiResponse.success(CodeRunResponse.builder()
			.fileId(fileId)
			.result(result)
			.success(!result.contains("[ERROR]"))
			.build());
	}
}
