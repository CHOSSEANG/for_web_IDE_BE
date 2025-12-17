package fs16.webide.web_ide_for.coding_session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CodingTimeRequestDto {

    private Long userId;
    private Long containerId;
    private Long codingTimeMs;
    private LocalDate recordDate;
}
