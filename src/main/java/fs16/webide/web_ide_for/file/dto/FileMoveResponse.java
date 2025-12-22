package fs16.webide.web_ide_for.file.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "파일/폴더 이동 결과")
public class FileMoveResponse {
	@Schema(description = "이동된 파일/폴더 ID")
	private Long fileId;

	@Schema(description = "파일명")
	private String fileName;

	@Schema(description = "변경된 부모 ID")
	private Long newParentId;

	@Schema(description = "변경된 전체 경로")
	private String newPath;

	@Schema(description = "디렉토리 여부")
	private Boolean isDirectory;

	@Schema(description = "수정 일시")
	private LocalDateTime updatedAt;

	@Schema(description = "설명")
	private String description;
}
