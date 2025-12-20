package fs16.webide.web_ide_for.container_member.entity;

import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "container_member",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"container_id","user_id"})
        }
)
public class ContainerMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // FK 필수
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "container_id", nullable = false)
    private Container container;
    @CreationTimestamp
    private LocalDateTime joined_at;

    public ContainerMember(User user, Container container) {
        this.user = user;
        this.container = container;
    }
}
