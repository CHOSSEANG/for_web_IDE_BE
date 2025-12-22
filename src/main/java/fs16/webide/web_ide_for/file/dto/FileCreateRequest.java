package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "파일 또는 디렉토리 생성 요청")
public class FileCreateRequest {
    @Schema(description = "컨테이너 ID", example = "1")
    private Long containerId;

    @Schema(description = "생성할 이름 (확장자 포함 시 파일로 인식)", example = "index.js")
    private String name;

    @Schema(description = "부모 디렉토리 ID (루트일 경우 null)", example = "5")
    private Long parentId;

    @Schema(description = "파일 초기 내용", example = "console.log('hello');")
    private String content;
}
