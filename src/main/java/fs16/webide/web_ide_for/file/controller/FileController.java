package fs16.webide.web_ide_for.file.controller;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.file.dto.FileCreateRequestDto;
import fs16.webide.web_ide_for.file.dto.FileCreateResponseDto;
import fs16.webide.web_ide_for.file.dto.FileTreeRequestDto;
import fs16.webide.web_ide_for.file.dto.FileTreeResponseDto;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequestDto;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponseDto;
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
     * @param requestDto The file creation request with content
     * @return The created file response
     */
    @PostMapping("/content")
    public ResponseEntity<ApiResponse<FileCreateResponseDto>> createFileWithContent(
            @RequestBody FileCreateRequestDto requestDto) {
        log.info("Creating file with content: {}", requestDto);
        FileCreateResponseDto responseDto = fileService.createFileWithContent(requestDto);
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

    /**
     * 파일 이름 또는 내용을 수정합니다.
     * * @param requestDto 수정할 파일의 ID, 새 이름, 새 내용 등을 포함한 DTO
     * @return 수정된 파일 정보와 결과 메시지
     */
    @PatchMapping("/update")
    public ResponseEntity<FileUpdateResponseDto> updateFile(@RequestBody FileUpdateRequestDto requestDto) {
        // FileService에 구현할 updateFile 메서드를 호출합니다.
        FileUpdateResponseDto response = fileService.updateFile(requestDto);
        return ResponseEntity.ok(response);
    }
}
