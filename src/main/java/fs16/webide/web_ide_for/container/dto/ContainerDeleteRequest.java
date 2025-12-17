package fs16.webide.web_ide_for.container.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 컨테이너 삭제 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerDeleteRequest {
    /**
     * 사용자 ID
     */
    private Long userId;

    /**
     * 삭제할 컨테이너 ID
     */
    private Long containerId;
}
