package fs16.webide.web_ide_for.chat.controller;

import com.github.benmanes.caffeine.cache.Cache;
import fs16.webide.web_ide_for.chat.dto.ChatRequest;
import fs16.webide.web_ide_for.chat.dto.ChatResponse;
import fs16.webide.web_ide_for.chat.service.ChatService;
import fs16.webide.web_ide_for.user.dto.UserInfoResponse;
import fs16.webide.web_ide_for.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Chat", description = "채팅 API")
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Cache<Long, UserInfoResponse> userCache;
    private final UserService userService;

    @Operation(summary = "채팅 조회", description = "7일 기간의 채팅을 조회합니다")
    @GetMapping("/chat")
    public List<ChatResponse> chatList(@RequestParam("containerId") Long containerId,
                                       @RequestParam(required = false) LocalDateTime lastCreatedAt){
        return chatService.chatList(containerId, lastCreatedAt);
    }

    @Operation(summary = "채팅 전송", description = "실시간으로 채팅을 보냅니다")
    @MessageMapping("/chat/{containerId}")
    public void send(@DestinationVariable Long containerId, ChatRequest msg, Principal principal) {
        Long userId = Long.valueOf(principal.getName());

        UserInfoResponse userInfoResponse = userService.getUserInfo(userId);

        ChatResponse response = new ChatResponse(
                userInfoResponse.getUserName(),
                userInfoResponse.getImageUrl(),
                msg.getMessage(),
                LocalDateTime.now()
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
