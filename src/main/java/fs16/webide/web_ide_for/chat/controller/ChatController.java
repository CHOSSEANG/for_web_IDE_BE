package fs16.webide.web_ide_for.chat.controller;

import fs16.webide.web_ide_for.chat.dto.ChatRequest;
import fs16.webide.web_ide_for.chat.dto.ChatResponse;
import fs16.webide.web_ide_for.chat.service.ChatService;
import fs16.webide.web_ide_for.user.dto.UserInfoResponse;
import fs16.webide.web_ide_for.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Tag(name = "Chat", description = "채팅 API")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserService userService;

    @GetMapping("/chat")
    public List<ChatResponse> chatList(
            @RequestParam("containerId") Long containerId,
            @RequestParam(required = false) String lastCreatedAt) {
        log.info("chat??????");
        LocalDateTime lastCreatedAtTime = null;
        try {
            if (lastCreatedAt != null && !lastCreatedAt.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                lastCreatedAtTime = LocalDateTime.parse(lastCreatedAt, formatter); // 변환 X
            }
        } catch (Exception e) {
            log.warn("lastCreatedAt 파싱 실패, 기본값 null 사용: {}", lastCreatedAt);
        }
        log.info("lastCreatedAtTime??????{}",lastCreatedAtTime);
        return chatService.chatList(containerId, lastCreatedAtTime);
    }

    @Operation(summary = "채팅 전송", description = "실시간으로 채팅을 보냅니다")
    @MessageMapping("/chat/{containerId}")
    public void send(@DestinationVariable Long containerId, ChatRequest msg, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        UserInfoResponse userInfo = userService.getUserInfo(userId);

        // 한국 시간 기준 LocalDateTime 생성 -> DB는 UTC로 저장
        LocalDateTime nowKST = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        chatService.saveMessageAsync(containerId, userId, nowKST, msg.getMessage());

        ChatResponse response = new ChatResponse(
                userId,
                userInfo.getUserName(),
                userInfo.getUserImgUrl(),
                msg.getMessage(),
                nowKST.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) // KST 문자열
        );

        simpMessagingTemplate.convertAndSend("/sub/chat/" + containerId, response);
    }

    @Operation(summary = "채팅 검색", description = "키워드로 검색한 채팅을 출력합니다")
    @GetMapping("/chat/search")
    public List<ChatResponse> searchChat(@RequestParam("containerId") Long containerId,
                                         @RequestParam("keyword") String keyword){
        return chatService.searchChat(containerId, keyword);
    }
}
