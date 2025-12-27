package fs16.webide.web_ide_for.container_member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberInviteRequest {
    private List<String> userIds;
}
