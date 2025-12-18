package fs16.webide.web_ide_for.container_member.repository;

import fs16.webide.web_ide_for.container_member.entity.ContainerMember;
import fs16.webide.web_ide_for.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContainerMemberRepository extends JpaRepository<ContainerMember, Long>  {

    @Query("""
            SELECT cm.userId
            FROM ContainerMember cm
            WHERE cm.containerId =:containerId
            """)
    List<Long> findUserIdsByContainerId(@Param("containerId") Long containerId);

    boolean existsByContainerIdAndUserId(Long containerId, Long userId);

    void deleteByContainerIdAndUserId(Long containerId, Long userId);

    void deleteByContainerId(Long containerId);
}
