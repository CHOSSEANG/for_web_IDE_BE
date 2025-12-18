package fs16.webide.web_ide_for.coding_session.repository;

import fs16.webide.web_ide_for.coding_session.entity.CodingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;

public interface CodingSessionRepository extends JpaRepository<CodingSession,Long> {

    @Query("""
            SELECT COALESCE(SUM(cs.codingTimeMs),0)
            FROM CodingSession cs
            WHERE cs.userId =:userId
            AND cs.recordDate  BETWEEN :startDate AND :endDate
            """)
    Long sumBetweenCodingTime(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
