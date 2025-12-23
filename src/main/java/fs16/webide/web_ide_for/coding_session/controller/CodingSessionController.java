package fs16.webide.web_ide_for.coding_session.controller;

import fs16.webide.web_ide_for.coding_session.dto.CodingTimeRequestDto;
import fs16.webide.web_ide_for.coding_session.dto.WeeklyCodingTimeDto;
import fs16.webide.web_ide_for.coding_session.service.CodingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "coding_session", description = "코딩 시간 측정 API")
@RestController
@RequestMapping("/code")
@Slf4j
public class CodingSessionController {

    private final CodingSessionService codingSessionService;

    public CodingSessionController(CodingSessionService codingSessionService) {
        this.codingSessionService = codingSessionService;
    }

    @Operation(summary = "코딩 시간 측정 통계", description = "주별 개인의 코딩시간을 측정한 통계를 볼 수 있습니다.")
    @GetMapping(value = "/coding-stats", produces = "application/json")
    public WeeklyCodingTimeDto getWeeklyCodingTime( @AuthenticationPrincipal Long userId){
        return codingSessionService.weeklyCodingTime(userId);
    }

    @Operation(summary = "코딩 시간 측정 저장", description = "개인의 코딩 시간을 저장합니다.")
    @PostMapping
    public String saveCodingTime(@RequestBody CodingTimeRequestDto dto, @AuthenticationPrincipal Long userId){
        codingSessionService.saveCodingTime(dto,userId);
        return "save Ok";
    }
}
