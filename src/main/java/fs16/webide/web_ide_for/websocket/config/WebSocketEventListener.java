package fs16.webide.web_ide_for.websocket.config;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.user.dto.UserInfoResponse;
import fs16.webide.web_ide_for.user.entity.User;
import fs16.webide.web_ide_for.user.error.UserErrorCode;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final UserRepository userRepository;
    private final Cache<Long, UserInfoResponse> userCache;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        // Principal에서 userId 가져오기
        if (accessor.getUser() != null) {
            Long userId = Long.valueOf(accessor.getUser().getName());

            // 캐시에 없으면 DB 조회 후 저장
            userCache.get(userId, id -> {
                User user = userRepository.findById(id)
                        .orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
                return new UserInfoResponse(user.getName(), user.getProfileImageUrl());
            });
        }
    }
}
