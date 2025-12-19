package fs16.webide.web_ide_for.file.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUpdateResponseDto {
    private Long fileId;
    private String fileName;
    private Long parentId;
    private Boolean isDirectory;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fileExtension;
    private String content;
    private String description;
}
