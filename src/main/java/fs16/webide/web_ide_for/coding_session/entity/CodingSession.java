package fs16.webide.web_ide_for.coding_session.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coding_session")
public class CodingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long containerId;
    private Long codingTimeMs;
    private LocalDate recordDate;
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public CodingSession() {
    }

    public CodingSession(Long userId, Long containerId, Long codingTimeMs, LocalDate recordDate) {
        this.userId = userId;
        this.containerId = containerId;
        this.codingTimeMs = codingTimeMs;
        this.recordDate = recordDate;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
