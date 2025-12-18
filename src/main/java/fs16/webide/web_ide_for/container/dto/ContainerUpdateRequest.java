package fs16.webide.web_ide_for.container.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 컨테이너 수정 요청 DTO
 */
@Schema(description = "컨테이너 수정 요청 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerUpdateRequest {
    /**
     * 사용자 ID
     */
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    /**
     * 수정할 컨테이너 ID
     */
    @Schema(description = "수정할 컨테이너 ID", example = "1")
    private Long containerId;

    /**
     * 새로운 컨테이너 이름
     */
    @Schema(description = "새로운 컨테이너 이름", example = "Updated Container Name")
    private String name;
}
