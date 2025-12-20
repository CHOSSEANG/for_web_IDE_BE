package fs16.webide.web_ide_for.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileRemoveRequest {
	private Long containerId; // 검증 및 S3 경로 확인용
}
