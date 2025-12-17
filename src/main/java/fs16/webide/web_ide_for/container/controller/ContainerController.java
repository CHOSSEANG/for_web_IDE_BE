package fs16.webide.web_ide_for.container.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.container.dto.ContainerCreateRequest;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.service.ContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/containers")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerService containerService;

    /**
     * 새로운 컨테이너를 생성합니다.
     * 
     * @param request 컨테이너 생성 요청 DTO
     * @return 생성된 컨테이너 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Container>> createContainer(@RequestBody ContainerCreateRequest request) {
        log.info("Container creation request received: {}", request);
        Container container = containerService.createContainer(request);
        return ResponseEntity.ok(ApiResponse.success(container));
    }
}
