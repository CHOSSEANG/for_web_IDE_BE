package fs16.webide.web_ide_for.container.dto;

import fs16.webide.web_ide_for.container.entity.Container;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 컨테이너 삭제 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerDeleteResponse {

    /**
     * 삭제된 컨테이너 ID
     */
    private Long id;

    /**
     * 삭제된 컨테이너 이름
     */
    private String name;

    /**
     * 삭제 성공 여부
     */
    private boolean success;

    /**
     * Container 엔티티를 ContainerDeleteResponse DTO로 변환합니다.
     * 
     * @param container 삭제된 Container 엔티티
     * @return ContainerDeleteResponse DTO
     */
    public static ContainerDeleteResponse from(Container container) {
        return ContainerDeleteResponse.builder()
                .id(container.getId())
                .name(container.getName())
                .success(true)
                .build();
    }
}
