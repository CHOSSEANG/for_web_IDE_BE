package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for requesting the file structure of a container
 */
@Getter
@Setter
@ToString
@Schema(description = "컨테이너 파일 트리 조회 요청")
public class FileTreeRequest {
    @Schema(description = "조회할 컨테이너 ID", example = "1")
    private Long containerId;
}
