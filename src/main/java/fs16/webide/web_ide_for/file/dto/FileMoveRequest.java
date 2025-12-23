package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "파일/폴더 이동 요청")
public class FileMoveRequest {

	@Schema(description = "도착지 부모 폴더 ID (루트 이동 시 null 가능)", example = "2")
	private Long targetParentId;
}
