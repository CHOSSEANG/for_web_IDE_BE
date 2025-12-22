package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "파일 상세 내용 로드 요청")
public class FileLoadRequest {
	@Schema(description = "파일 ID", example = "10")
	private Long fileId;
}
