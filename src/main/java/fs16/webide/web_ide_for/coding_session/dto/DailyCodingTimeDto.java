package fs16.webide.web_ide_for.coding_session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DailyCodingTimeDto {
    private LocalDate todayDate;
    private Long codingTimeMs;
}
