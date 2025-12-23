package fs16.webide.web_ide_for.file.repository;

import fs16.webide.web_ide_for.file.entity.ContainerFile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<ContainerFile, Long> {
    List<ContainerFile> findByContainerId(Long containerId);
    List<ContainerFile> findByParentId(Long parentId); // Keep for backward compatibility
    List<ContainerFile> findByParent(ContainerFile parent); // New method using entity reference
    Optional<ContainerFile> findByContainerIdAndPath(Long containerId, String path);
    List<ContainerFile> findByContainerIdAndParentId(Long containerId, Long parentId); // Keep for backward compatibility
    List<ContainerFile> findByContainerIdAndParent(Long containerId, ContainerFile parent); // New method using entity reference
}
