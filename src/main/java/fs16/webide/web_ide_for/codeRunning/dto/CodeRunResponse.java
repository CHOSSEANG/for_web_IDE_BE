package fs16.webide.web_ide_for.codeRunning.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeRunResponse {
	private String output;
	private boolean success;
	private String errorMessage;
}
