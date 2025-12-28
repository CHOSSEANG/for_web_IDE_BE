package fs16.webide.web_ide_for.chat.dto;

import fs16.webide.web_ide_for.chat.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private Long userId;
    private String userName;
    private String userImgUrl;
    private String message;
    private String createdAt; // 한국 시간 문자열

    public static ChatResponse fromEntity(Chat chat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedCreatedAt = chat.getCreatedAt().format(formatter); // 변환 없이 포맷만

        return new ChatResponse(
                chat.getSender().getId(),
                chat.getSender().getName(),
                chat.getSender().getProfileImageUrl(),
                chat.getMessage(),
                formattedCreatedAt
        );
    }

}
