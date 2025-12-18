package fs16.webide.web_ide_for.user.repository;

import fs16.webide.web_ide_for.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByClerkId(String clerkId);

    @Query("""
            SELECT u
            FROM User u
            WHERE (u.name LIKE %:keyword%
            OR u.clerkId =:keyword)
            AND u.status = 'Y'
            """)
    List<User> searchNameOrClerkId(@Param("keyword")String keyword);
}
