package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for representing a file or directory in a file structure tree
 */
@Getter
@Setter
@Builder
@Schema(description = "파일 트리 노드 정보")
public class FileTreeResponse {
    @Schema(description = "파일/디렉토리 ID")
    private Long id;

    @Schema(description = "이름", example = "src")
    private String name;

    @Schema(description = "경로", example = "/src")
    private String path;

    @Schema(description = "디렉토리 여부")
    private Boolean isDirectory;

    @Schema(description = "확장자", example = "folder")
    private String extension;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "하위 파일/폴더 목록 (재귀 구조)")
    private List<FileTreeResponse> children;
}
