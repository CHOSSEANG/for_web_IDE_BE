package fs16.webide.web_ide_for.container.dto;

import java.time.LocalDateTime;

import fs16.webide.web_ide_for.container.entity.Container;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 컨테이너 수정 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerUpdateResponse {

    // 컨테이너 ID
    private Long id;

    // 컨테이너 이름
    private String name;

    // 소유자 ID
    private Long userId;

    // 생성 시간
    private LocalDateTime createdAt;

    // Container 엔티티로부터 응답 DTO를 생성하는 정적 팩토리 메서드
    public static ContainerUpdateResponse from(Container container) {
        return ContainerUpdateResponse.builder()
                .id(container.getId())
                .name(container.getName())
                .userId(container.getUser().getId())
                .createdAt(container.getCreatedAt())
                .build();
    }
}
