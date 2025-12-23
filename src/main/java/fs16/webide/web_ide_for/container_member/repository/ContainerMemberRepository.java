package fs16.webide.web_ide_for.container_member.repository;

import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container_member.entity.ContainerMember;
import fs16.webide.web_ide_for.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContainerMemberRepository extends JpaRepository<ContainerMember, Long>  {

    @Query("SELECT cm.user.id FROM ContainerMember cm WHERE cm.container.id = :containerId")
    List<Long> findUserIdsByContainerId(@Param("containerId") Long containerId);

    boolean existsByContainerIdAndUserId(Long containerId, Long userId);

    void deleteByContainerIdAndUserId(Long containerId, Long userId);

    void deleteByContainerId(Long containerId);

    @Query("SELECT cm.user FROM ContainerMember cm WHERE cm.container.id = :containerId")
    List<User> findUsersByContainerId(@Param("containerId") Long containerId);

    void deleteAllByUser(User user);

    @Query("SELECT cm.container FROM ContainerMember cm WHERE cm.user.id = :userId")
    List<Container> findContainerByUserId(@Param("userId") Long userId);
}
