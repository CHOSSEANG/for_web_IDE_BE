package fs16.webide.web_ide_for.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String clerkId;
    private String userName;
    private String userImgUrl;
}
