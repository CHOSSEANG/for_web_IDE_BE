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

        // DB에 저장된 KST LocalDateTime 그대로 쓰되, 명시적으로 KST 기준으로 포맷
        String formattedCreatedAt = chat.getCreatedAt()
                .atZone(ZoneId.systemDefault()) // 서버 기본 시간대
                .withZoneSameInstant(ZoneId.of("Asia/Seoul")) // KST로 변환
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
