package fs16.webide.web_ide_for.file.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "파일 상세 내용 및 메타데이터")
public class FileLoadResponse {
	@Schema(description = "파일 ID")
	private Long fileId;

	@Schema(description = "파일명")
	private String fileName;

	@Schema(description = "파일 경로")
	private String filePath;

	@Schema(description = "파일 본문 내용 (S3 로드 데이터)")
	private String content;

	@Schema(description = "확장자")
	private String extension;

	@Schema(description = "마지막 수정 시간")
	private LocalDateTime updatedAt;

	@Schema(description = "설명")
	private String description;
}
