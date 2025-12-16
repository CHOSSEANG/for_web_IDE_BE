package fs16.webide.web_ide_for.clerk.controller;

import fs16.webide.web_ide_for.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clerk")
@RequiredArgsConstructor
@Slf4j
public class ClerkWebhookController {

    private final UserService userService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String,Object> payload){
//        log.info("Webhook payload: {}", payload);

        String eventType = (String) payload.get("type");
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

        if (eventType == null || data == null) {
            log.warn("Invalid webhook payload");
            return ResponseEntity.badRequest().body("invalid payload");
        }

        // Clerk user id
        String clerkUserId = (String) data.get("id");

        switch (eventType) {
            case "user.created":
                userService.findOrCreateUser(clerkUserId,data);
                break;

            case "user.updated":
                userService.updateUser(clerkUserId,data);
                break;

            case "user.deleted":
                userService.deleteUser(clerkUserId);
                break;

            default:
                log.info("Unhandled Clerk event: {}", eventType);
        }

        return ResponseEntity.ok("ok");
    }
}
