package fs16.webide.web_ide_for.file.controller;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.file.dto.FileCreateRequest;
import fs16.webide.web_ide_for.file.dto.FileCreateResponse;
import fs16.webide.web_ide_for.file.dto.FileMoveRequest;
import fs16.webide.web_ide_for.file.dto.FileMoveResponse;
import fs16.webide.web_ide_for.file.dto.FileRemoveRequest;
import fs16.webide.web_ide_for.file.dto.FileRemoveResponse;
import fs16.webide.web_ide_for.file.dto.FileTreeResponse;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequest;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponse;
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
     * 파일 또는 디렉토리를 생성합니다.
     * name에 '.'이 포함되어 있으면 파일로, 없으면 디렉토리로 판단합니다.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<FileCreateResponse>> createFile(
        @RequestBody FileCreateRequest requestDto) {
        log.info("Creating file or directory with name: {}", requestDto.getName());
        FileCreateResponse responseDto = fileService.createFile(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }

    /**
     * 내용을 포함하여 파일을 생성합니다.
     */
    @PostMapping("/content")
    public ResponseEntity<ApiResponse<FileCreateResponse>> createFileWithContent(
        @RequestBody FileCreateRequest requestDto) {
        log.info("Creating file with content. Name: {}", requestDto.getName());
        FileCreateResponse responseDto = fileService.createFileWithContent(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }

    /**
     * Retrieves the file structure of a container
     * @param containerId The ID of the container
     * @return The file structure as a tree
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<List<FileTreeResponse>>> getFileTree(
            @RequestParam Long containerId) {
        log.info("Getting file tree for container: {}", containerId);
        List<FileTreeResponse> fileStructure = fileService.getFileStructure(containerId);
        return ResponseEntity.ok(ApiResponse.success(fileStructure));
    }

    /**
     * 파일 이름 또는 내용을 수정합니다.
     * * @param requestDto 수정할 파일의 ID, 새 이름, 새 내용 등을 포함한 DTO
     * @return 수정된 파일 정보와 결과 메시지
     */
    @PatchMapping("/update")
    public ResponseEntity<FileUpdateResponse> updateFile(@RequestBody FileUpdateRequest requestDto) {
        // FileService에 구현할 updateFile 메서드를 호출합니다.
        FileUpdateResponse response = fileService.updateFile(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 파일 또는 폴더를 다른 디렉토리로 이동합니다.
     * @param requestDto 이동할 파일 ID와 목적지 부모 ID를 포함한 DTO
     * @return 이동 완료 후 갱신된 파일 정보
     */
    @PatchMapping("/move")
    public ResponseEntity<FileMoveResponse> moveFile(@RequestBody FileMoveRequest requestDto) {
        // FileService에 구현할 moveFile 메서드를 호출합니다.
        FileMoveResponse response = fileService.moveFile(requestDto);
        return ResponseEntity.ok(response);
    }

    /**
     * 파일 또는 폴더를 삭제합니다.
     * @param fileId 삭제할 파일의 ID (Path Variable)
     * @param containerId 컨테이너 ID (Query Parameter)
     * @return 삭제된 파일 정보
     */
    @DeleteMapping("/remove/{fileId}")
    public ResponseEntity<ApiResponse<FileRemoveResponse>> removeFile(
        @PathVariable("fileId") Long fileId,
        @RequestParam("containerId") Long containerId) {

        log.info("Removing file/directory. ID: {}, Container: {}", fileId, containerId);

        // 서비스 메서드 호출 시 각각의 인자로 전달
        FileRemoveResponse response = fileService.removeFile(fileId, containerId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
