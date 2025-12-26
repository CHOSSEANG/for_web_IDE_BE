package fs16.webide.web_ide_for.codeRunning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "코드 실행 결과 응답 객체")
public class CodeRunResponse {
	@Schema(description = "실행된 파일의 고유 ID", example = "31")
	private Long fileId;

	@Schema(description = "코드 실행 출력 결과 (표준 출력 및 에러 포함)", example = "Hello World from Python!")
	private String result;

	@Schema(description = "실행 성공 여부 (시스템 에러 발생 시 false)", example = "true")
	private boolean success;
}
