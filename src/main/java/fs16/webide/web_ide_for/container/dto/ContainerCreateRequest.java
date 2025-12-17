package fs16.webide.web_ide_for.container.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // 기본 생성자 (JSON 역직렬화에 필요)
@AllArgsConstructor // 모든 필드를 포함하는 생성자 (테스트 또는 Builder 패턴 구현 시 유용)
public class ContainerCreateRequest {

	// 1. 컨테이너 이름 (필수)
	private String name;

	// 2. 소유자 ID (필수)
	// NOTE: 실제 서비스에서는 이 필드 대신 로그인된 사용자 세션/토큰에서 ID를 가져오는 것이 보안상 더 좋습니다.
	private Long userId;

	// 3. 기반이 될 도커 이미지 태그 (필수)
	private String imageTag;

	// 4. 컨테이너에 할당할 메모리 (선택, 기본값 설정 가능)
	private Integer memoryLimitMb;

	// 5. 컨테이너에 대한 간단한 설명 (선택)
	private String description;
}
