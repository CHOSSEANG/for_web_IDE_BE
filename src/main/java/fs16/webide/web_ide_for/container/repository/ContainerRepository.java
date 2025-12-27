package fs16.webide.web_ide_for.container.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fs16.webide.web_ide_for.container.entity.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

	// 사용자가 속한 컨테이너를 페이징하여 조회
	@Query("SELECT c FROM Container c " +
		"JOIN ContainerMember cm ON c.id = cm.container.id " +
		"WHERE cm.user.id = :userId")
	Page<Container> findContainersByUserId(@Param("userId") Long userId, Pageable pageable);

	// 검색 기능 추가 (선택사항)
	@Query("SELECT c FROM Container c " +
		"JOIN ContainerMember cm ON c.id = cm.container.id " +
		"WHERE cm.user.id = :userId " +
		"AND LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Container> findContainersByUserIdAndNameContaining(
		@Param("userId") Long userId,
		@Param("keyword") String keyword,
		Pageable pageable
	);
}
