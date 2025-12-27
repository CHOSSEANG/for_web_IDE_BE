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
import org.springframework.data.domain.Page;

/**
 * 컨테이너 목록 페이징 조회 응답 DTO
 */
@Schema(description = "컨테이너 목록 페이징 조회 응답 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerListResponse {

    // ===== 컨테이너 데이터 =====
    @Schema(description = "컨테이너 목록")
    private List<ContainerInfo> containers;

    // ===== 페이징 메타데이터 =====
    @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    private int currentPage;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "전체 요소 수", example = "50")
    private long totalElements;

    @Schema(description = "페이지 크기", example = "10")
    private int pageSize;

    @Schema(description = "다음 페이지 존재 여부", example = "true")
    private boolean hasNext;

    @Schema(description = "이전 페이지 존재 여부", example = "false")
    private boolean hasPrevious;

    @Schema(description = "첫 페이지 여부", example = "true")
    private boolean isFirst;

    @Schema(description = "마지막 페이지 여부", example = "false")
    private boolean isLast;

    /**
     * 컨테이너 정보 내부 클래스
     */
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContainerInfo {
        @Schema(description = "컨테이너 ID", example = "1")
        private Long id;

        @Schema(description = "컨테이너 이름", example = "My Container")
        private String name;

        @Schema(description = "컨테이너 언어", example = "JAVA")
        private String lang;

        @Schema(description = "컨테이너 생성 시간", example = "2023-01-01T12:00:00")
        private LocalDateTime createdAt;

        public static ContainerInfo from(Container container) {
            return ContainerInfo.builder()
                .id(container.getId())
                .name(container.getName())
                .lang(container.getLang())
                .createdAt(container.getCreatedAt())
                .build();
        }
    }

    /**
     * Page<Container>를 ContainerListResponse로 변환
     */
    public static ContainerListResponse from(Page<Container> containerPage) {
        return ContainerListResponse.builder()
            .containers(containerPage.getContent().stream()
                .map(ContainerInfo::from)
                .collect(Collectors.toList()))
            .currentPage(containerPage.getNumber())
            .totalPages(containerPage.getTotalPages())
            .totalElements(containerPage.getTotalElements())
            .pageSize(containerPage.getSize())
            .hasNext(containerPage.hasNext())
            .hasPrevious(containerPage.hasPrevious())
            .isFirst(containerPage.isFirst())
            .isLast(containerPage.isLast())
            .build();
    }
}
