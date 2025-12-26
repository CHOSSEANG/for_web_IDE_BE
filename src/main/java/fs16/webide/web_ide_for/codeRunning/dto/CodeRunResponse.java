package fs16.webide.web_ide_for.codeRunning.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CodeRunResponse {
	private Long fileId;
	private String result;
	private boolean success;
}
