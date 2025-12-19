package fs16.webide.web_ide_for.container.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import fs16.webide.web_ide_for.user.entity.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 생성일 자동화를 위해 추가
@Table(name = "container") // 테이블명을 명시하는 것이 좋습니다.
public class Container {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY) // ManyToOne 관계는 LAZY 로딩 권장
	@JoinColumn(name = "user_id", nullable = false) // user_id는 보통 필수값이므로 nullable = false 추가
	private User user; // 필드명은 카멜 케이스로 유지

	@Column(nullable = false, length = 100) // 컨테이너 이름은 필수값이므로 nullable = false, 길이 제한 권장
	private String name;

	@CreatedDate
	@Column(updatable = false) // 생성 시간은 업데이트되지 않도록 설정
	private LocalDateTime createdAt; // Java 8 Time API 사용 및 카멜 케이스

	@Builder
	public Container(User user, String name) {
		this.user = user;
		this.name = name;
		// created_at은 @CreatedDate가 자동 주입합니다.
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

}
