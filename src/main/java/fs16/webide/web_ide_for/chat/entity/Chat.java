package fs16.webide.web_ide_for.chat.entity;

import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;

    @Column(nullable = false,length = 1000)
    private String message;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // OffsetDateTime → LocalDateTime으로 변경
}
