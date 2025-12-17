package fs16.webide.web_ide_for.coding_session.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodingTimeRequestDto {

    private Long userId;
    private Long containerId;
    private Long codingTimeMs;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
}
