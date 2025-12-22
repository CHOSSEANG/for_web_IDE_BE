package fs16.webide.web_ide_for.file.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileLoadResponse {
	private Long fileId;
	private String fileName;
	private String filePath;
	private String content;      // S3에서 읽어온 파일 내용
	private String extension;
	private LocalDateTime updatedAt;
	private String description;
}
