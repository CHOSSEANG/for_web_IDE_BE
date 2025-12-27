package fs16.webide.web_ide_for.container.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 컨테이너 목록 조회를 위한 페이징 요청 DTO
 */
@Schema(description = "컨테이너 목록 조회를 위한 페이징 요청 DTO")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContainerListRequest {

    /**
     * 페이지 번호 (0부터 시작)
     */
    @Schema(description = "페이지 번호 (0부터 시작)", example = "0", defaultValue = "0")
    @Builder.Default
    private int page = 0;

    /**
     * 페이지 크기
     */
    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Builder.Default
    private int size = 10;

    /**
     * 정렬 기준 필드
     */
    @Schema(description = "정렬 기준 필드", example = "name", defaultValue = "name")
    @Builder.Default
    private String sortBy = "name";

    /**
     * 정렬 방향 (ASC/DESC)
     */
    @Schema(description = "정렬 방향 (ASC/DESC)", example = "ASC", defaultValue = "ASC")
    @Builder.Default
    private String sortDirection = "ASC";

}
