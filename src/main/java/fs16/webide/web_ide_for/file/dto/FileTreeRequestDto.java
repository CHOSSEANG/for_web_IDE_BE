package fs16.webide.web_ide_for.file.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for requesting the file structure of a container
 */
@Getter
@Setter
@ToString
public class FileTreeRequestDto {
    private Long containerId;
}
