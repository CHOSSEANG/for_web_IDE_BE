package fs16.webide.web_ide_for.container_member.service;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.error.ContainerErrorCode;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.container_member.entity.ContainerMember;
import fs16.webide.web_ide_for.container_member.repository.ContainerMemberRepository;
import fs16.webide.web_ide_for.user.entity.User;
import fs16.webide.web_ide_for.user.error.UserErrorCode;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static fs16.webide.web_ide_for.common.error.CommonErrorCode.BAD_REQUEST;


@Service
@RequiredArgsConstructor
@Transactional
public class ContainerMemberService {

    private final ContainerMemberRepository containerMemberRepository;
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;


    // 단일 초대
    public void inviteMember(Long containerId, Long userId) {

        if(containerMemberRepository.existsByContainerIdAndUserId(containerId,userId)){
            throw new CoreException(UserErrorCode.USER_EXISTED);
        }
        // User, Container 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new CoreException(ContainerErrorCode.CONTAINER_NOT_FOUND));
        ContainerMember containerMember = new ContainerMember(user, container);
        containerMemberRepository.save(containerMember);
    }


    // 여러명 초대
    public void invitedMembers(Long containerId, List<Long> userIds){
        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new CoreException(ContainerErrorCode.CONTAINER_NOT_FOUND));
        Set<Long> invitedUserIds = new HashSet<>(containerMemberRepository.findUserIdsByContainerId(containerId));

        List<User> users = userIds.stream()
                .filter(id -> !invitedUserIds.contains(id))
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND)))
                .toList();

        if(users.isEmpty()){
            return;
        }
        List<ContainerMember> memberToSave = users.stream()
                        .map(user -> new ContainerMember(user, container))
                        .toList();

        containerMemberRepository.saveAll(memberToSave);

    }

    // 컨테이너 나가기
    public void leaveContainer(Long containerId, Long userId){
        containerMemberRepository.deleteByContainerIdAndUserId(containerId,userId);
    }

    // 컨테이너 삭제 시 해당 컨테이너 유저들도 컨테이너 멤버에서 삭제
    public void removeContainerMembers(Long containerId){

        Set<Long> users = new HashSet<>(containerMemberRepository.findUserIdsByContainerId(containerId));

        containerMemberRepository.deleteByContainerId(containerId);

        List<Long> userIds = users.stream().toList();
    }

    public List<User> findUsersByContainer(Long containerId) {
        return containerMemberRepository.findUsersByContainerId(containerId);
    }
}
