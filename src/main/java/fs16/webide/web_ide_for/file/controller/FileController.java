package fs16.webide.web_ide_for.file.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.file.dto.FileCreateRequest;
import fs16.webide.web_ide_for.file.dto.FileCreateResponse;
import fs16.webide.web_ide_for.file.dto.FileLoadRequest;
import fs16.webide.web_ide_for.file.dto.FileLoadResponse;
import fs16.webide.web_ide_for.file.dto.FileMoveRequest;
import fs16.webide.web_ide_for.file.dto.FileMoveResponse;
import fs16.webide.web_ide_for.file.dto.FileRemoveResponse;
import fs16.webide.web_ide_for.file.dto.FileTreeResponse;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequest;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponse;
import fs16.webide.web_ide_for.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public ApiResponse<FileCreateResponse> createFile(
        @RequestBody FileCreateRequest requestDto) {
        log.info("Creating file or directory with name: {}", requestDto.getName());
        FileCreateResponse responseDto = fileService.createFile(requestDto);
        return ApiResponse.success(responseDto);
    }

    /**
     * Retrieves the file structure of a container
     * @param containerId The ID of the container
     * @return The file structure as a tree
     */
    @GetMapping("/tree")
    public ApiResponse<List<FileTreeResponse>> getFileTree(
            @RequestParam Long containerId) {
        log.info("Getting file tree for container: {}", containerId);
        List<FileTreeResponse> fileStructure = fileService.getFileStructure(containerId);
        return ApiResponse.success(fileStructure);
    }

    /**
     * 특정 파일의 상세 내용(S3에 저장된 실제 코드/텍스트)을 조회합니다.
     * @param fileId 파일 ID
     * @return 파일 정보와 S3에서 읽어온 content가 포함된 DTO
     */
    @GetMapping("/{fileId}/content")
    public ApiResponse<FileLoadResponse> getFileContent(@PathVariable("fileId") Long fileId) {
        log.info("Loading content for file ID: {}", fileId);

        // FileService를 통해 DB 메타데이터와 S3의 실제 내용을 가져옵니다.
        FileLoadResponse response = fileService.getFileContent(fileId);

        return ApiResponse.success(response);
    }

    /**
     * 파일 이름 또는 내용을 수정합니다.
     * * @param requestDto 수정할 파일의 ID, 새 이름, 새 내용 등을 포함한 DTO
     * @return 수정된 파일 정보와 결과 메시지
     */
    @PatchMapping("/update")
    public ApiResponse<FileUpdateResponse> updateFile(@RequestBody FileUpdateRequest requestDto) {
        // FileService에 구현할 updateFile 메서드를 호출합니다.
        FileUpdateResponse response = fileService.updateFile(requestDto);
        return ApiResponse.success(response);
    }

    /**
     * 파일 또는 폴더를 다른 디렉토리로 이동합니다.
     * @param requestDto 이동할 파일 ID와 목적지 부모 ID를 포함한 DTO
     * @return 이동 완료 후 갱신된 파일 정보
     */
    @PatchMapping("/move")
    public ApiResponse<FileMoveResponse> moveFile(@RequestBody FileMoveRequest requestDto) {
        // FileService에 구현할 moveFile 메서드를 호출합니다.
        FileMoveResponse response = fileService.moveFile(requestDto);
        return ApiResponse.success(response);
    }

    /**
     * 파일 또는 폴더를 삭제합니다.
     * @param fileId 삭제할 파일의 ID (Path Variable)
     * @param containerId 컨테이너 ID (Query Parameter)
     * @return 삭제된 파일 정보
     */
    @DeleteMapping("/remove/{fileId}")
    public ApiResponse<FileRemoveResponse> removeFile(
        @PathVariable("fileId") Long fileId,
        @RequestParam("containerId") Long containerId) {

        log.info("Removing file/directory. ID: {}, Container: {}", fileId, containerId);

        // 서비스 메서드 호출 시 각각의 인자로 전달
        FileRemoveResponse response = fileService.removeFile(fileId, containerId);

        return ApiResponse.success(response);
    }
}
