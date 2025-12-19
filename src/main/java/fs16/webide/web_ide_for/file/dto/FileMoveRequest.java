package fs16.webide.web_ide_for.file.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMoveRequest {
	private Long fileId;       // 이동할 파일/폴더의 ID
	private Long targetParentId; // 이동될 대상 폴더의 ID (루트인 경우 null 가능)}
}
