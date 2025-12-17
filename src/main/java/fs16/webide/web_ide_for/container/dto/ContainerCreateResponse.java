package fs16.webide.web_ide_for.container.dto;

import java.time.LocalDateTime;

import fs16.webide.web_ide_for.container.entity.Container;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "컨테이너 생성 응답 DTO")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerCreateResponse {

	// 컨테이너 ID
	@Schema(description = "컨테이너 ID", example = "1")
	private Long id;

	// 컨테이너 이름
	@Schema(description = "컨테이너 이름", example = "My Container")
	private String name;

	// 소유자 ID (User 객체 전체가 아닌 ID만 반환)
	@Schema(description = "소유자 ID", example = "1")
	private Long userId;

	// 생성 시간
	@Schema(description = "생성 시간", example = "2023-01-01T12:00:00")
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
