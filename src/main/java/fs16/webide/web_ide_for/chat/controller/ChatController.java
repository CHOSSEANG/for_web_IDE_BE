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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

        OffsetDateTime lastCreatedAtTime = null;
        if (lastCreatedAt != null && !lastCreatedAt.isEmpty()) {
            // 프론트가 보내는 "yyyy-MM-dd HH:mm:ss" 형태 파싱
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastCreatedAtTime = LocalDateTime.parse(lastCreatedAt, formatter)
                    .atOffset(ZoneOffset.UTC); // UTC 기준으로 변환
        }

        return chatService.chatList(containerId, lastCreatedAtTime);
    }

    @Operation(summary = "채팅 전송", description = "실시간으로 채팅을 보냅니다")
    @MessageMapping("/chat/{containerId}")
    public void send(@DestinationVariable Long containerId, ChatRequest msg, Principal principal) {
        Long userId = Long.valueOf(principal.getName());
        UserInfoResponse userInfo = userService.getUserInfo(userId);

        // 한국 시간 기준 OffsetDateTime 생성
        OffsetDateTime nowKST = OffsetDateTime.now(ZoneOffset.ofHours(9));

        ChatResponse response = new ChatResponse(
                userId,
                userInfo.getUserName(),
                userInfo.getUserImgUrl(),
                msg.getMessage(),
                nowKST.toString() // 한국 시간 문자열
        );

        chatService.saveMessageAsync(containerId, userId, msg.getMessage());
        simpMessagingTemplate.convertAndSend("/sub/chat/" + containerId, response);
    }

    @Operation(summary = "채팅 검색", description = "키워드로 검색한 채팅을 출력합니다")
    @GetMapping("/chat/search")
    public List<ChatResponse> searchChat(@RequestParam("containerId") Long containerId,
                                         @RequestParam("keyword") String keyword){
        return chatService.searchChat(containerId, keyword);
    }
}
