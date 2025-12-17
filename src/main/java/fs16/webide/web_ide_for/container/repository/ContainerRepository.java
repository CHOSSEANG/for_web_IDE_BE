package fs16.webide.web_ide_for.container.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fs16.webide.web_ide_for.container.entity.Container;

@Repository
public interface ContainerRepository extends JpaRepository<Container, Long> {

}
