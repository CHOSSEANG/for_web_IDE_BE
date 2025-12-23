package fs16.webide.web_ide_for.user.controller;

import fs16.webide.web_ide_for.user.dto.UserSearchResponse;
import fs16.webide.web_ide_for.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "User", description = "유저 API")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "로그인/회원가입", description = "jwt 유효검사를 통해 신규 생성 및 조회를 합니다")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("로그인 성공");
    }


    @Operation(summary = "유저 검색", description = "초대할 유저를 키워드로 검색합니다")
    @GetMapping("/search")
    public List<UserSearchResponse> findUsers(@RequestParam Long containerId ,@RequestParam String keyword) {
        return userService.findUsers(containerId,keyword);
    }



    @Operation(summary = "프로필 변경", description = "이름, 프로필 이미지를 변경할 수 있습니다")
    @PostMapping("/update")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String,Object> payload){

        String eventType = (String) payload.get("type");
        Object dataObj = payload.get("data");
        if (!(dataObj instanceof Map<?, ?>)) {
            log.warn("Invalid data type in webhook payload");
            return ResponseEntity.badRequest().body("invalid payload");
        }

        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (eventType == null || data == null) {
            log.warn("Invalid webhook payload");
            return ResponseEntity.badRequest().body("invalid payload");
        }

        // Clerk user id
        String clerkUserId = (String) data.get("id");
        Long userId = userService.getUserIdByClerkId(clerkUserId);

        switch (eventType) {
            case "user.created":
                userService.findOrCreateUser(clerkUserId,data);
                break;

            case "user.updated":
                userService.updateUser(userId,data);
                break;

            case "user.deleted":
                userService.deleteUser(userId);
                break;

            default:
                log.info("Unhandled Clerk event: {}", eventType);
        }

        return ResponseEntity.ok("ok");
    }



}
