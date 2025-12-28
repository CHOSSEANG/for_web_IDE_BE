package fs16.webide.web_ide_for.chat.dto;

import fs16.webide.web_ide_for.chat.entity.Chat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String userId;
    private String userName;
    private String userImgUrl;
    private String message;
    private String createdAt; // 한국 시간 문자열

    public static ChatResponse fromEntity(Chat chat) {
        ZoneId kstZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime kstTime = chat.getCreatedAt().atZone(ZoneId.systemDefault())
                .withZoneSameInstant(kstZone);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("date????{}",chat.getCreatedAt().format(formatter));
        return new ChatResponse(
                chat.getSender().getClerkId(),
                chat.getSender().getName(),
                chat.getSender().getProfileImageUrl(),
                chat.getMessage(),
                chat.getCreatedAt().format(formatter) // DB에 저장된 KST 그대로
        );
    }


}
