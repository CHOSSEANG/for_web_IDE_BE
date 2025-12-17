package fs16.webide.web_ide_for.coding_session.contoller;

import fs16.webide.web_ide_for.coding_session.dto.CodingTimeRequestDto;
import fs16.webide.web_ide_for.coding_session.dto.WeeklyCodingTimeDto;
import fs16.webide.web_ide_for.coding_session.service.CodingSessionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/code")
public class CodingSessionController {

    private final CodingSessionService codingSessionService;

    public CodingSessionController(CodingSessionService codingSessionService) {
        this.codingSessionService = codingSessionService;
    }

    @GetMapping("/coding-stats/{userId}")
    public WeeklyCodingTimeDto getWeeklyCodingTime(@PathVariable Long userId){
        return codingSessionService.weeklyCodingTime(userId);
    }

    @PostMapping
    public String saveCodingTime(CodingTimeRequestDto dto){
        codingSessionService.saveCodingTime(dto);
        return "save Ok";
    }
}
