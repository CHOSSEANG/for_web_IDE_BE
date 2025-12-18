package fs16.webide.web_ide_for.container_member.service;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.container_member.entity.ContainerMember;
import fs16.webide.web_ide_for.container_member.repository.ContainerMemberRepository;
import fs16.webide.web_ide_for.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static fs16.webide.web_ide_for.common.error.CommonErrorCode.BAD_REQUEST;


@Service
@RequiredArgsConstructor
@Transactional
public class ContainerMemberService {

    private final ContainerMemberRepository containerMemberRepository;
    private final NotificationService notificationService;
    private final ContainerRepository containerRepository;

    // 단건 알림
    private void pushAfterCommit(Long userId, String msg){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                notificationService.push(userId,msg);
            }
        });
    }

    // 다건 알림
    private void pushAfterCommit(List<Long> userIds, String msg){
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                userIds.forEach(userId->notificationService.push(userId,msg));
            }
        });
    }

    // 단일 초대
    public void inviteMember(Long containerId, Long userId) {

        if(containerMemberRepository.existsByContainerIdAndUserId(containerId,userId)){
            throw new CoreException(BAD_REQUEST, "이미 초대된 유저입니다.") ;
        }

        //컨테이너 이름 조회
        String containerName = containerRepository.findNameById(containerId);

        ContainerMember containerMember = new ContainerMember(containerId,userId);

        //알림
        pushAfterCommit(userId,"["+containerName+"] 컨테이너에 초대되었습니다.");
        containerMemberRepository.save(containerMember);
    }


    // 여러명 초대
    public void invitedMembers(Long containerId, List<Long> userIds){

        Set<Long> invitedUserIds = new HashSet<>(containerMemberRepository.findUserIdsByContainerId(containerId));

        // 컨테이너 이름 조회
        String containerName = containerRepository.findNameById(containerId);

        List<Long> users = userIds.stream()
                .filter(userId->!invitedUserIds.contains(userId))
                .toList();

        if(users.isEmpty()){
            return;
        }

        List<ContainerMember> memberToSave = users.stream()
                        .map(userId-> new ContainerMember(containerId,userId))
                        .toList();

        containerMemberRepository.saveAll(memberToSave);

        pushAfterCommit(users,"["+containerName+"] 컨테이너에 초대되었습니다.");
    }

    // 컨테이너 나가기
    public void leaveContainer(Long containerId, Long userId){
        containerMemberRepository.deleteByContainerIdAndUserId(containerId,userId);
    }

    // 컨테이너 삭제 시 해당 컨테이너 유저들도 컨테이너 멤버에서 삭제
    public void removeContainerMembers(Long containerId){

        Set<Long> users = new HashSet<>(containerMemberRepository.findUserIdsByContainerId(containerId));

        String containerName = containerRepository.findNameById(containerId);

        containerMemberRepository.deleteByContainerId(containerId);

        List<Long> userIds = users.stream().toList();
        pushAfterCommit(userIds,"["+containerName+"] 컨테이너가 삭제되었습니다.");
    }
}
