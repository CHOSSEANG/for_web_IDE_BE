package fs16.webide.web_ide_for.chat.dto;

import fs16.webide.web_ide_for.chat.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

        // DB에 저장된 UTC LocalDateTime -> KST로 변환
        LocalDateTime createdAt = chat.getCreatedAt();
        String formattedCreatedAt = createdAt
                .atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime()
                .format(formatter);

        return new ChatResponse(
                chat.getSender().getId(),
                chat.getSender().getName(),
                chat.getSender().getProfileImageUrl(),
                chat.getMessage(),
                formattedCreatedAt
        );
    }
}
