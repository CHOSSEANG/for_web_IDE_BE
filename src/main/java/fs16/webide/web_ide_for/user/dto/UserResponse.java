package fs16.webide.web_ide_for.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private boolean success;
    private String message;

    public static UserResponse success(String message) {
        return new UserResponse(true, message);
    }
}