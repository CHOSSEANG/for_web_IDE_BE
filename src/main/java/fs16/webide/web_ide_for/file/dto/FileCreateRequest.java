package fs16.webide.web_ide_for.file.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCreateRequest {
    private Long containerId;
    private String name;
    private Long parentId;
    private Boolean isDirectory;
    private String fileExtension;
    private String content;
}
