package fs16.webide.web_ide_for.file.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpdateRequest {
    private Long fileId;
    private String newName;
    private String newContent;
}
