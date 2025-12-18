package fs16.webide.web_ide_for.file.repository;

import fs16.webide.web_ide_for.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByContainerId(Long containerId);
    List<File> findByParentId(Long parentId); // Keep for backward compatibility
    List<File> findByParent(File parent); // New method using entity reference
    Optional<File> findByContainerIdAndPath(Long containerId, String path);
    List<File> findByContainerIdAndParentId(Long containerId, Long parentId); // Keep for backward compatibility
    List<File> findByContainerIdAndParent(Long containerId, File parent); // New method using entity reference
}
