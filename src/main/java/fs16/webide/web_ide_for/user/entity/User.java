package fs16.webide.web_ide_for.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String clerkId;
    private String name;
    @Column(name="profile_image_url")
    private String profileImageUrl;
    private String status = "Y";

}
