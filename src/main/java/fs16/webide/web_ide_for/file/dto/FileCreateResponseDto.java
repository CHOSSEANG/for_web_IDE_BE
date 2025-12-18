package fs16.webide.web_ide_for.file.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileCreateResponseDto {
    private Long id;
    private Long containerId;
    private String fileName;
    private Long parentDirectoryId;
    private Boolean isDirectory;
    private String filePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fileLanguage;
}
