package fs16.webide.web_ide_for.container.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 단일 컨테이너 조회 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerFindRequest {
    private Long userId;

    private Long containerId;
}
