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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContainerMemberService {

    private final ContainerMemberRepository containerMemberRepository;
    private final ContainerRepository containerRepository;
    private final UserRepository userRepository;

    // 초대
    public void invitedMembers(Long containerId, List<String> userIds){

        Container container = containerRepository.findById(containerId)
                .orElseThrow(() -> new CoreException(ContainerErrorCode.CONTAINER_NOT_FOUND));
        Set<String> invitedClerkIds = new HashSet<>(containerMemberRepository.findClerkIdsByContainerId(containerId));

        List<String> newIds = userIds.stream()
                .filter(id -> !invitedClerkIds.contains(id))
                .toList();
        List<User> users = userRepository.findAllByClerkIdIn(newIds);
        if (newIds.size() != users.size()) throw new CoreException(UserErrorCode.USER_NOT_FOUND);

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

    public List<Container> findContainersByUser(Long userId) {
        return containerMemberRepository.findContainersByUserId(userId);
    }
}
