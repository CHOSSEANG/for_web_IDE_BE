package fs16.webide.web_ide_for.file.controller;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.file.dto.FileCreateRequestDto;
import fs16.webide.web_ide_for.file.dto.FileCreateResponseDto;
import fs16.webide.web_ide_for.file.dto.FileTreeRequestDto;
import fs16.webide.web_ide_for.file.dto.FileTreeResponseDto;
import fs16.webide.web_ide_for.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    /**
     * Creates a file or directory
     * @param requestDto The file creation request
     * @return The created file response
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FileCreateResponseDto>> createFile(
            @RequestBody FileCreateRequestDto requestDto) {
        log.info("Creating file: {}", requestDto);
        FileCreateResponseDto responseDto = fileService.createFile(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    /**
     * Creates a file with content
     * @param requestDto The file creation request
     * @param content The content of the file
     * @return The created file response
     */
    @PostMapping("/content")
    public ResponseEntity<ApiResponse<FileCreateResponseDto>> createFileWithContent(
            @RequestBody FileCreateRequestDto requestDto,
            @RequestParam String content) {
        log.info("Creating file with content: {}", requestDto);
        FileCreateResponseDto responseDto = fileService.createFileWithContent(requestDto, content);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    /**
     * Retrieves the file structure of a container
     * @param containerId The ID of the container
     * @return The file structure as a tree
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<FileTreeResponseDto>>> getFileTree(
            @RequestParam Long containerId) {
        log.info("Getting file tree for container: {}", containerId);
        List<FileTreeResponseDto> fileStructure = fileService.getFileStructure(containerId);
        return ResponseEntity.ok(ApiResponse.success(fileStructure));
    }
}
