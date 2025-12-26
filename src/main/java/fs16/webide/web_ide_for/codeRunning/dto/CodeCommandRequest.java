package fs16.webide.web_ide_for.codeRunning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeCommandRequest {
	@Schema(description = "실행할 쉘 명령어", example = "ls -al")
	private String code;
}
