package fs16.webide.web_ide_for.clerk;

import fs16.webide.web_ide_for.chat.error.ChatErrorCode;
import fs16.webide.web_ide_for.clerk.service.ClerkJwtService;
import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;

@Slf4j
@Component
public class StompJwtChannelInterceptor implements ChannelInterceptor {

    private static final String USER_ID_KEY = "USER_ID";

    private final ClerkJwtService clerkJwtService;
    private final UserRepository userRepository;

    public StompJwtChannelInterceptor(
            ClerkJwtService clerkJwtService,
            UserRepository userRepository
    ) {
        this.clerkJwtService = clerkJwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // accessor 없으면 바로 통과
        if (accessor == null) {
            return message;
        }

        // CONNECT가 아니면 아무 것도 하지 말고 통과
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        // 여기부터는 CONNECT일 때만 실행됨

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new CoreException(ChatErrorCode.HEADER_NOT_FOUND);
        }

        String token = authHeader.substring(7);
        Map<String, Object> claims = clerkJwtService.validate(token);

        String clerkId = claims.get("sub").toString();

        Long userId = userRepository.findByClerkId(clerkId)
                .orElseThrow()
                .getId();

        Principal principal =
                new UsernamePasswordAuthenticationToken(
                        String.valueOf(userId),
                        null,
                        null
                );

        accessor.setUser(principal);
        accessor.getSessionAttributes().put("USER_ID", userId);

        return message;
    }
}
