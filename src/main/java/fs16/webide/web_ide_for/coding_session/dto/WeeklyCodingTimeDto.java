package fs16.webide.web_ide_for.coding_session.dto;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class WeeklyCodingTimeDto {
    private List<DailyCodingTimeDto> daily;
    private Long avgWeeklyCodingTime;
    private Long maxWeeklyCodingTime;
    private Long totalWeeklyCodingTime;
}
