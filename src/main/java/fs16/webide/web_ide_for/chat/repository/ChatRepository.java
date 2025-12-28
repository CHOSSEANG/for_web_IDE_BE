package fs16.webide.web_ide_for.chat.repository;

import fs16.webide.web_ide_for.chat.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // 채팅 메세지 조회 (페이징)
    @Query("""
            SELECT c
            FROM Chat c
            LEFT JOIN FETCH c.sender s
            WHERE c.container.id = :containerId
              AND c.createdAt >= :oneWeekAgo
              AND (:lastCreatedAt IS NULL OR c.createdAt < :lastCreatedAt)
            ORDER BY c.createdAt DESC
        """)
    List<Chat> getChatList(
            @Param("containerId") Long containerId,
            @Param("lastCreatedAt") OffsetDateTime lastCreatedAt,
            @Param("oneWeekAgo") OffsetDateTime oneWeekAgo,
            Pageable pageable
    );

    // 채팅 검색
    @Query("""
            SELECT c
            FROM Chat c
            JOIN FETCH c.sender s
            WHERE c.container.id = :containerId
              AND (:keyword IS NULL OR c.message LIKE %:keyword%)
            ORDER BY c.createdAt DESC
        """)
    List<Chat> searchChat(
            @Param("containerId") Long containerId,
            @Param("keyword") String keyword
    );
}
