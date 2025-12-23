package fs16.webide.web_ide_for.file.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "files")
public class ContainerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "container_id")
    private Long containerId;
    
    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id") // DB 컬럼명
    private ContainerFile parent; // 부모 파일(폴더) 객체 직접 참조

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ContainerFile> children = new ArrayList<>(); // 하위 파일들 목록
    
    @Column(name = "is_directory")
    private Boolean isDirectory;
    
    @Column(name = "path")
    private String path;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "extension")
    private String extension;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
