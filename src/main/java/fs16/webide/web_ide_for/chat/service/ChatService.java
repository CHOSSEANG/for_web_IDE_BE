package fs16.webide.web_ide_for.chat.service;

import fs16.webide.web_ide_for.chat.dto.ChatResponse;
import fs16.webide.web_ide_for.chat.entity.Chat;
import fs16.webide.web_ide_for.chat.repository.ChatRepository;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ChatService {
    private static final int CHAT_PAGE_SIZE = 20;

    private final ChatRepository chatRepository;
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository, ContainerRepository containerRepository, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.containerRepository = containerRepository;
        this.userRepository = userRepository;
    }

    // 지난 일주일간 채팅 메세지 조회
    public List<ChatResponse> chatList(Long containerId, LocalDateTime lastCreatedAt){
       Pageable pageable = PageRequest.of(0,CHAT_PAGE_SIZE);
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Chat> chats = chatRepository.getChatList(
                containerId, lastCreatedAt, oneWeekAgo,pageable
        );
        Collections.reverse(chats);
        return chats.stream().map(chat -> new ChatResponse(
                chat.getSender().getName(),
                chat.getSender().getProfileImageUrl(),
                chat.getMessage(),
                chat.getCreatedAt()
        )).toList();
    }

    // 채팅 메세지 저장
    @Async
    public void saveMessageAsync(Long containerId, Long userId, String message) {
        try {
            Chat chat = Chat.builder()
                    .container(containerRepository.getReferenceById(containerId))
                    .sender(userRepository.getReferenceById(userId))
                    .message(message)
                    .build();

            chatRepository.save(chat);
        } catch (Exception e) {
            // Async는 예외처리 불가
            log.error("채팅 저장 실패 containerId={}, userId={}", containerId, userId, e);
        }

    }


    // 채팅 검색
    @Transactional(readOnly = true)
    public List<ChatResponse> searchChat(Long containerId, String keyword){

        List<Chat> chats = chatRepository.searchChat(containerId, keyword);
        Collections.reverse(chats);
        return chats.stream().map(chat -> new ChatResponse(
                chat.getSender().getName(),
                chat.getSender().getProfileImageUrl(),
                chat.getMessage(),
                chat.getCreatedAt())
        ).toList();

    }
}
