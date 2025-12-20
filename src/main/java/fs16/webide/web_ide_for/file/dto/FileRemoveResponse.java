package fs16.webide.web_ide_for.file.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileRemoveResponse {
	private Long fileId;
	private String fileName;
	private String filePath;
	private String description;
}
