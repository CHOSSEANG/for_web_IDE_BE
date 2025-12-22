package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "파일 또는 디렉토리 생성 결과")
public class FileCreateResponse {
    @Schema(description = "생성된 파일/디렉토리 ID")
    private Long id;

    @Schema(description = "소속 컨테이너 ID")
    private Long containerId;

    @Schema(description = "파일명", example = "index.js")
    private String fileName;

    @Schema(description = "부모 디렉토리 ID")
    private Long parentDirectoryId;

    @Schema(description = "디렉토리 여부")
    private Boolean isDirectory;

    @Schema(description = "S3 또는 전체 경로", example = "/src/index.js")
    private String filePath;

    @Schema(description = "생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "파일 확장자", example = "js")
    private String fileExtension;

    @Schema(description = "상태 설명", example = "파일이 성공적으로 생성되었습니다.")
    private String description;
}
