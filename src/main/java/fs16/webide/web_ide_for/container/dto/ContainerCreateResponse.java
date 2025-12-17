package fs16.webide.web_ide_for.container.dto;

import java.time.LocalDateTime;

import fs16.webide.web_ide_for.container.entity.Container;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerCreateResponse {

	// 컨테이너 ID
	private Long id;

	// 컨테이너 이름
	private String name;

	// 소유자 ID (User 객체 전체가 아닌 ID만 반환)
	private Long userId;

	// 생성 시간
	private LocalDateTime createdAt;

	// Container 엔티티로부터 응답 DTO를 생성하는 정적 팩토리 메서드
	public static ContainerCreateResponse from(Container container) {
		return ContainerCreateResponse.builder()
			.id(container.getId())
			.name(container.getName())
			.userId(container.getUser().getId())
			.build();
	}
}
