package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "파일 수정 결과 정보")
public class FileUpdateResponse {
    @Schema(description = "파일 ID")
    private Long fileId;

    @Schema(description = "수정된 파일명", example = "updated_script.js")
    private String fileName;

    @Schema(description = "부모 디렉토리 ID")
    private Long parentId;

    @Schema(description = "디렉토리 여부")
    private Boolean isDirectory;

    @Schema(description = "파일의 전체 경로", example = "/src/updated_script.js")
    private String filePath;

    @Schema(description = "최초 생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "최종 수정 일시")
    private LocalDateTime updatedAt;

    @Schema(description = "파일 확장자", example = "js")
    private String fileExtension;

    @Schema(description = "수정된 파일 내용")
    private String content;

    @Schema(description = "결과 메시지", example = "파일이 성공적으로 수정되었습니다.")
    private String description;
}
