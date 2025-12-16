package fs16.webide.web_ide_for.user.repository;

import fs16.webide.web_ide_for.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClerkId(String clerkId);
}
