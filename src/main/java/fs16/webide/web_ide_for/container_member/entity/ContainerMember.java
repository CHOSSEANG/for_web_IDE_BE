package fs16.webide.web_ide_for.container_member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "container_name",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"container_id","user_id"})
        }
)
public class ContainerMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userI_id", nullable = false)
    private Long userId;
    @Column(name = "container_id", nullable = false)
    private Long containerId;
    @CreationTimestamp
    private LocalDateTime joined_at;

    public ContainerMember(Long userId, Long containerId) {
        this.userId = userId;
        this.containerId = containerId;
    }
}
