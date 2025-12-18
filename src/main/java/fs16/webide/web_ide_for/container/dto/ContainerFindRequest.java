package fs16.webide.web_ide_for.container.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 단일 컨테이너 조회 요청 DTO
 */
@Schema(description = "단일 컨테이너 조회 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerFindRequest {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "컨테이너 ID", example = "1")
    private Long containerId;
}
