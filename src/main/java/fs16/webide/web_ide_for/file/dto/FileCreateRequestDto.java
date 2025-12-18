package fs16.webide.web_ide_for.file.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileCreateRequestDto {
    private Long containerId;
    private String name;
    private Long parentId;
    private Boolean isDirectory;
    private String filePath;
    private String fileExtension;
    private String content;
}
