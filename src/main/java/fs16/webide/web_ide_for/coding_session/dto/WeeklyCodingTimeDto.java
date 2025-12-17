package fs16.webide.web_ide_for.coding_session.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyCodingTimeDto {
    private List<DailyCodingTimeDto> daily;
    private Long avgWeeklyCodingTime;
    private Long maxWeeklyCodingTime;
    private Long totalWeeklyCodingTime;
}
