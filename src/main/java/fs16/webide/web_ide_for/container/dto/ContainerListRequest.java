package fs16.webide.web_ide_for.container.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 ID를 기반으로 컨테이너 목록을 조회하기 위한 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ContainerListRequest {

    /**
     * 컨테이너 목록을 조회할 사용자의 ID
     */
    private Long userId;
}
