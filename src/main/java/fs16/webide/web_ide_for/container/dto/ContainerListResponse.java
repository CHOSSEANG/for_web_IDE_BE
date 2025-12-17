package fs16.webide.web_ide_for.container.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import fs16.webide.web_ide_for.container.entity.Container;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 컨테이너 목록 조회 응답 DTO
 */
@Schema(description = "컨테이너 목록 조회 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerListResponse {

    /**
     * 컨테이너 ID
     */
    @Schema(description = "컨테이너 ID", example = "1")
    private Long id;

    /**
     * 컨테이너 이름
     */
    @Schema(description = "컨테이너 이름", example = "My Container")
    private String name;

    /**
     * 컨테이너 생성 시간
     */
    @Schema(description = "컨테이너 생성 시간", example = "2023-01-01T12:00:00")
    private LocalDateTime createdAt;

    /**
     * Container 엔티티를 ContainerListResponse DTO로 변환합니다.
     * 
     * @param container Container 엔티티
     * @return ContainerListResponse DTO
     */
    public static ContainerListResponse from(Container container) {
        return ContainerListResponse.builder()
                .id(container.getId())
                .name(container.getName())
                .createdAt(container.getCreatedAt())
                .build();
    }

    /**
     * Container 엔티티 목록을 ContainerListResponse DTO 목록으로 변환합니다.
     * 
     * @param containers Container 엔티티 목록
     * @return ContainerListResponse DTO 목록
     */
    public static List<ContainerListResponse> fromList(List<Container> containers) {
        return containers.stream()
                .map(ContainerListResponse::from)
                .collect(Collectors.toList());
    }
}
