package fs16.webide.web_ide_for.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "파일 이름 또는 내용 수정 요청")
public class FileUpdateRequest {

    @Schema(description = "변경할 파일 이름 (변경하지 않을 경우 기존 이름 전달)", example = "updated_script.js")
    private String newName;

    @Schema(description = "변경할 파일 내용 (텍스트 형식)", example = "console.log('updated content');")
    private String newContent;
}
