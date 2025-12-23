package fs16.webide.web_ide_for.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {
    private String userName;
    private String userImgUrl;
    private String message;
    private LocalDateTime createdAt;
}
