package fs16.webide.web_ide_for.container.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fs16.webide.web_ide_for.container.entity.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

    /**
     * 특정 사용자의 모든 컨테이너를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 사용자가 소유한 컨테이너 목록
     */
    List<Container> findAllByUserId(Long userId);
}
