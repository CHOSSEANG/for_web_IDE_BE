package fs16.webide.web_ide_for.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSearchResponse {

    private Long userId;
    private String clerkId;
    private String name;
    private String profileImageUrl;
    private boolean invited;

}
