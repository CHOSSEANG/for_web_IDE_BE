package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "파일/폴더 삭제 결과")
public class FileRemoveResponse {
	@Schema(description = "삭제된 파일 ID")
	private Long fileId;

	@Schema(description = "삭제된 파일명")
	private String fileName;

	@Schema(description = "삭제된 파일 경로")
	private String filePath;

	@Schema(description = "설명", example = "파일이 성공적으로 삭제되었습니다.")
	private String description;
}
