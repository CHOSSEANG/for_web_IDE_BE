package fs16.webide.web_ide_for.container.dto;

import fs16.webide.web_ide_for.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "컨테이너 생성 요청 DTO")
@Getter
@NoArgsConstructor // 기본 생성자 (JSON 역직렬화에 필요)
@AllArgsConstructor // 모든 필드를 포함하는 생성자 (테스트 또는 Builder 패턴 구현 시 유용)
public class ContainerCreateRequest {

	// 1. 컨테이너 이름 (필수)
	@Schema(description = "컨테이너 이름", example = "My Container")
	private String name;

	// 2. 소유자 ID (필수)
	// NOTE: 실제 서비스에서는 이 필드 대신 로그인된 사용자 세션/토큰에서 ID를 가져오는 것이 보안상 더 좋습니다.
	@Schema(description = "소유자 정보")
	private User user;
}
