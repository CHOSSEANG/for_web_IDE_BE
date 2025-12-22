package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파일/폴더 삭제 요청")
public class FileRemoveRequest {
	@Schema(description = "컨테이너 ID (권한 확인용)", example = "1")
	private Long containerId;
}
