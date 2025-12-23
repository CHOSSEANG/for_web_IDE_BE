package fs16.webide.web_ide_for.container.controller;

import java.util.List;
import java.util.stream.Collectors;

import fs16.webide.web_ide_for.container.dto.*;
import fs16.webide.web_ide_for.container_member.dto.MemberInviteRequest;
import fs16.webide.web_ide_for.container_member.service.ContainerMemberService;
import fs16.webide.web_ide_for.user.dto.UserInfoResponse;
import fs16.webide.web_ide_for.user.entity.User;
import fs16.webide.web_ide_for.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fs16.webide.web_ide_for.common.ApiResponse;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.service.ContainerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Container", description = "컨테이너 관리 API")
@Slf4j
@RestController
@RequestMapping("/container")
@RequiredArgsConstructor
public class ContainerController {

    private final ContainerService containerService;
    private final ContainerMemberService containerMemberService;
    private final UserService userService;

    /**
     * 새로운 컨테이너를 생성합니다.
     * 
     * @param request 컨테이너 생성 요청 DTO
     * @return 생성된 컨테이너 정보
     */
    @Operation(summary = "컨테이너 생성", description = "새로운 컨테이너를 생성합니다")
    @PostMapping("/create")
    public ApiResponse<ContainerCreateResponse> createContainer(@RequestBody ContainerCreateRequest request,@AuthenticationPrincipal Long userId) {
        log.info("Container creation request received: {}", request);
        User user = userService.getUserById(userId);
        Container container = containerService.createContainer(request, user);
        return ApiResponse.success(ContainerCreateResponse.from(container));
    }

    /**
     * 특정 사용자의 모든 컨테이너를 조회합니다.
     * 
     * @param userId 컨테이너 목록을 조회할 사용자의 ID
     * @return 사용자가 소유한 컨테이너 목록
     */
    @Operation(summary = "컨테이너 목록 조회", description = "특정 사용자의 모든 컨테이너를 조회합니다")
    @GetMapping("/list")
    public ApiResponse<List<ContainerListResponse>> findAllContainers(@AuthenticationPrincipal Long userId) {
        log.info("Container list request received for user ID: {}", userId);
        ContainerListRequest request = new ContainerListRequest(userId);
        List<Container> containers = containerMemberService.findContainersByUser(userId);
        return ApiResponse.success(ContainerListResponse.fromList(containers));
    }

    /**
     * 특정 ID의 컨테이너를 조회합니다.
     * 
     * @param containerId 조회할 컨테이너의 ID
     * @return 조회된 컨테이너 정보
     */
    @Operation(summary = "컨테이너 조회", description = "특정 ID의 컨테이너를 조회합니다")
    @GetMapping("/{containerId}")
    public ApiResponse<ContainerFindResponse> findContainer(@AuthenticationPrincipal Long userId, @PathVariable Long containerId) {
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
    @Operation(summary = "컨테이너 삭제", description = "특정 ID의 컨테이너를 삭제합니다")
    @DeleteMapping("/{containerId}")
    public ApiResponse<ContainerDeleteResponse> deleteContainer(@AuthenticationPrincipal Long userId, @PathVariable Long containerId) {
        log.info("Container delete request received for container ID: {}", containerId);
        ContainerDeleteRequest request = new ContainerDeleteRequest(userId, containerId);
        Container container = containerService.deleteContainer(request);
        return ApiResponse.success(ContainerDeleteResponse.from(container));
    }

    /**
     * 특정 ID의 컨테이너 이름을 수정합니다.
     * 
     * @param userId 사용자 ID
     * @param containerId 수정할 컨테이너의 ID
     * @param request 컨테이너 수정 요청 DTO
     * @return 수정된 컨테이너 정보
     */
    @Operation(summary = "컨테이너 수정", description = "특정 ID의 컨테이너 이름을 수정합니다")
    @PutMapping("/{containerId}")
    public ApiResponse<ContainerUpdateResponse> updateContainer(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long containerId, 
            @RequestBody ContainerUpdateRequest request) {
        log.info("Container update request received for container ID: {}", containerId);
        // 경로 변수와 요청 본문의 값을 통합
        ContainerUpdateRequest updateRequest = new ContainerUpdateRequest(userId, containerId, request.getName());
        Container container = containerService.updateContainer(updateRequest);
        return ApiResponse.success(ContainerUpdateResponse.from(container));
    }


    /**
     * 선택한 유저(들)를 컨테이너에 초대합니다
     *
     * @param containerId 초대된 컨테이너의 ID
     * @param request 컨테이너에 초대된 userIds
     * @return "초대 완료"
     */
    @Operation(summary = "컨테이너 초대", description = "선택한 유저(들)를 컨테이너에 초대합니다")
    @PostMapping("/{containerId}/invite")
    public ApiResponse<String> inviteMember(
            @PathVariable Long containerId,
            @RequestBody MemberInviteRequest request) {

        containerMemberService.invitedMembers(containerId, request.getUserIds());

        return ApiResponse.success("초대 완료");
    }

    /**
     * 해당 컨테이너에서 나갑니다
     *
     * @param containerId 나갈 컨테이너의 ID
     * userId 컨테이너에서 나갈 user ID
     * @return "컨테이너 나가기 완료"
     */
    @Operation(summary = "컨테이너 나가기", description = "해당 컨테이너에서 나갑니다")
    @DeleteMapping("/{containerId}/leave")
    public ApiResponse<String> leaveContainer(
            @PathVariable Long containerId,
            @AuthenticationPrincipal Long userId) {
        containerMemberService.leaveContainer(containerId, userId);
        return ApiResponse.success("컨테이너 나가기 완료");
    }

    @Operation(summary = "채팅 참여자 정보 조회", description = "채팅에 참여하고 있는 유저의 이름과 이미지가 보여집니다.")
    @GetMapping("/{containerId}/users")
    public List<UserInfoResponse> getUsersByContainer(@PathVariable Long containerId) {
        List<User> users = containerMemberService.findUsersByContainer(containerId); // 컨테이너에 속한 유저만 조회

        return users.stream()
                .map(user -> new UserInfoResponse(
                        user.getName(),
                        user.getProfileImageUrl()
                ))
                .collect(Collectors.toList());
    }

}
