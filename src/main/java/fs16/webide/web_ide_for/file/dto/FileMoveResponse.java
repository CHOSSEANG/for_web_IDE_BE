package fs16.webide.web_ide_for.file.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMoveResponse {
	private Long fileId;
	private String fileName;
	private Long newParentId;
	private String newPath;
	private Boolean isDirectory;
	private LocalDateTime updatedAt;
	private String description;
}
