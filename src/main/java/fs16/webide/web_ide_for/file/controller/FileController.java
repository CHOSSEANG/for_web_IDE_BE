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
import fs16.webide.web_ide_for.file.dto.FileLoadResponse;
import fs16.webide.web_ide_for.file.dto.FileMoveRequest;
import fs16.webide.web_ide_for.file.dto.FileMoveResponse;
import fs16.webide.web_ide_for.file.dto.FileRemoveResponse;
import fs16.webide.web_ide_for.file.dto.FileTreeResponse;
import fs16.webide.web_ide_for.file.dto.FileUpdateRequest;
import fs16.webide.web_ide_for.file.dto.FileUpdateResponse;
import fs16.webide.web_ide_for.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Tag(name = "File API", description = "파일 및 디렉토리 관리(생성, 조회, 수정, 이동, 삭제)를 위한 API")
public class FileController {

    private final FileService fileService;

    /**
     * 파일 또는 디렉토리를 생성합니다.
     * name에 '.'이 포함되어 있으면 파일로, 없으면 디렉토리로 판단합니다.
     */
    @Operation(summary = "파일/디렉토리 생성", description = "새로운 파일이나 디렉토리를 생성합니다. name에 '.' 포함 여부로 타입을 구분합니다.")
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
    @Operation(summary = "파일 트리 조회", description = "특정 컨테이너 내의 모든 파일과 폴더를 트리 구조로 조회합니다.")
    @GetMapping("/tree")
    public ApiResponse<List<FileTreeResponse>> getFileTree(
        @Parameter(description = "컨테이너 ID", example = "1") @RequestParam Long containerId) {
        log.info("Getting file tree for container: {}", containerId);
        List<FileTreeResponse> fileStructure = fileService.getFileStructure(containerId);
        return ApiResponse.success(fileStructure);
    }

    /**
     * 특정 파일의 상세 내용(S3에 저장된 실제 코드/텍스트)을 조회합니다.
     * @param fileId 파일 ID
     * @return 파일 정보와 S3에서 읽어온 content가 포함된 DTO
     */
    @Operation(summary = "파일 내용 조회", description = "파일의 내용을 불러옵니다.")
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
    @Operation(summary = "파일 수정", description = "파일의 이름이나 본문 내용을 수정합니다.")
    @PatchMapping("/{fileId}/update")
    public ApiResponse<FileUpdateResponse> updateFile(@PathVariable("fileId") Long fileId
        ,@RequestBody FileUpdateRequest requestDto) {
        // FileService에 구현할 updateFile 메서드를 호출합니다.
        FileUpdateResponse response = fileService.updateFile(fileId,requestDto);
        return ApiResponse.success(response);
    }

    /**
     * 파일 또는 폴더를 다른 디렉토리로 이동합니다.
     * @param fileId 이동할 파일 ID
     * @param requestDto 목적지 부모 ID를 포함한 DTO
     * @return 이동 완료 후 갱신된 파일 정보
     */
    @Operation(summary = "파일/폴더 이동", description = "파일이나 디렉토리를 지정된 부모 디렉토리 하위로 이동시킵니다.")
    @PatchMapping("/{fileId}/move")
    public ApiResponse<FileMoveResponse> moveFile(@PathVariable Long fileId, @RequestBody FileMoveRequest requestDto) {
        // FileService에 구현할 moveFile 메서드를 호출합니다.
        FileMoveResponse response = fileService.moveFile(fileId, requestDto);
        return ApiResponse.success(response);
    }

    /**
     * 파일 또는 폴더를 삭제합니다.
     * @param fileId 삭제할 파일의 ID (Path Variable)
     * @param containerId 컨테이너 ID (Query Parameter)
     * @return 삭제된 파일 정보
     */
    @Operation(summary = "파일/폴더 삭제", description = "특정 파일 또는 디렉토리를 삭제합니다.")
    @DeleteMapping("/{fileId}/remove")
    public ApiResponse<FileRemoveResponse> removeFile(
        @Parameter(description = "삭제할 파일 ID", example = "10") @PathVariable("fileId") Long fileId,
        @Parameter(description = "소속 컨테이너 ID", example = "1") @RequestParam("containerId") Long containerId) {

        log.info("Removing file/directory. ID: {}, Container: {}", fileId, containerId);

        // 서비스 메서드 호출 시 각각의 인자로 전달
        FileRemoveResponse response = fileService.removeFile(fileId, containerId);

        return ApiResponse.success(response);
    }
}
