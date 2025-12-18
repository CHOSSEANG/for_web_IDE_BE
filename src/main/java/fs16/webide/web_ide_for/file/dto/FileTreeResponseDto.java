package fs16.webide.web_ide_for.file.dto;

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
public class FileTreeResponseDto {
    private Long id;
    private String name;
    private String path;
    private Boolean isDirectory;
    private String extension;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileTreeResponseDto> children; // Recursive structure for directories
}
