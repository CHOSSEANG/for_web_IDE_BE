package fs16.webide.web_ide_for.container.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.container.dto.ContainerCreateRequest;
import fs16.webide.web_ide_for.container.dto.ContainerCreateResponse;
import fs16.webide.web_ide_for.container.dto.ContainerDeleteRequest;
import fs16.webide.web_ide_for.container.dto.ContainerDeleteResponse;
import fs16.webide.web_ide_for.container.dto.ContainerFindRequest;
import fs16.webide.web_ide_for.container.dto.ContainerFindResponse;
import fs16.webide.web_ide_for.container.dto.ContainerListRequest;
import fs16.webide.web_ide_for.container.dto.ContainerListResponse;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.service.ContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/container")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerService containerService;

    /**
     * 새로운 컨테이너를 생성합니다.
     * 
     * @param request 컨테이너 생성 요청 DTO
     * @return 생성된 컨테이너 정보
     */
    @PostMapping("/create")
    public ApiResponse<ContainerCreateResponse> createContainer(@RequestBody ContainerCreateRequest request) {
        log.info("Container creation request received: {}", request);
        Container container = containerService.createContainer(request);
        return ApiResponse.success(ContainerCreateResponse.from(container));
    }

    /**
     * 특정 사용자의 모든 컨테이너를 조회합니다.
     * 
     * @param userId 컨테이너 목록을 조회할 사용자의 ID
     * @return 사용자가 소유한 컨테이너 목록
     */
    @GetMapping("/list")
    public ApiResponse<List<ContainerListResponse>> findAllContainers(@RequestParam Long userId) {
        log.info("Container list request received for user ID: {}", userId);
        ContainerListRequest request = new ContainerListRequest(userId);
        List<Container> containers = containerService.findAllContainersByUserId(request);
        return ApiResponse.success(ContainerListResponse.fromList(containers));
    }

    /**
     * 특정 ID의 컨테이너를 조회합니다.
     * 
     * @param containerId 조회할 컨테이너의 ID
     * @return 조회된 컨테이너 정보
     */
    @GetMapping("/{containerId}")
    public ApiResponse<ContainerFindResponse> findContainer(@RequestParam Long userId, @PathVariable Long containerId) {
        log.info("Container get request received for container ID: {}", containerId);
        ContainerFindRequest request = new ContainerFindRequest(userId, containerId);
        Container container = containerService.findContainer(request);
        return ApiResponse.success(ContainerFindResponse.from(container));
    }

    /**
     * 특정 ID의 컨테이너를 삭제합니다.
     * 
     * @param userId 사용자 ID
     * @param containerId 삭제할 컨테이너의 ID
     * @return 삭제된 컨테이너 정보
     */
    @DeleteMapping("/{containerId}")
    public ApiResponse<ContainerDeleteResponse> deleteContainer(@RequestParam Long userId, @PathVariable Long containerId) {
        log.info("Container delete request received for container ID: {}", containerId);
        ContainerDeleteRequest request = new ContainerDeleteRequest(userId, containerId);
        Container container = containerService.deleteContainer(request);
        return ApiResponse.success(ContainerDeleteResponse.from(container));
    }
}
