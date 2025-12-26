package fs16.webide.web_ide_for.codeRunning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "단순 쉘 명령어 실행 결과 응답 객체")
public class CodeCommandResponse {
	@Schema(description = "명령어 실행 결과 출력 (Standard Output)", example = "total 12\ndrwxr-xr-x 2 ubuntu ubuntu 4096...")
	private String output;

	@Schema(description = "명령어 실행 성공 여부", example = "true")
	private boolean success;

	@Schema(description = "명령어 실행 실패 시 발생한 에러 메시지", example = "bash: command not found", nullable = true)
	private String errorMessage;
}
