package fs16.webide.web_ide_for.container.dto;

import fs16.webide.web_ide_for.container.entity.Container;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 단일 컨테이너 조회 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerFindResponse {

    /**
     * 컨테이너 ID
     */
    private Long id;

    /**
     * 컨테이너 이름
     */
    private String name;

    /**
     * Container 엔티티를 ContainerGetResponse DTO로 변환합니다.
     * 
     * @param container Container 엔티티
     * @return ContainerGetResponse DTO
     */
    public static ContainerFindResponse from(Container container) {
        return ContainerFindResponse.builder()
                .id(container.getId())
                .name(container.getName())
                .build();
    }
}
