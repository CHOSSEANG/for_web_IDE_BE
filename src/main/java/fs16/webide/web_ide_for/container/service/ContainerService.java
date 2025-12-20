package fs16.webide.web_ide_for.container.service;

import static fs16.webide.web_ide_for.container.error.ContainerErrorCode.*;
import static fs16.webide.web_ide_for.common.error.CommonErrorCode.NOT_FOUND;

import java.util.List;
import java.util.NoSuchElementException;

import fs16.webide.web_ide_for.container_member.entity.ContainerMember;
import fs16.webide.web_ide_for.container_member.repository.ContainerMemberRepository;
import fs16.webide.web_ide_for.container_member.service.ContainerMemberService;
import org.springframework.stereotype.Service;

import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container.dto.ContainerCreateRequest;
import fs16.webide.web_ide_for.container.dto.ContainerDeleteRequest;
import fs16.webide.web_ide_for.container.dto.ContainerFindRequest;
import fs16.webide.web_ide_for.container.dto.ContainerListRequest;
import fs16.webide.web_ide_for.container.dto.ContainerUpdateRequest;
import fs16.webide.web_ide_for.container.entity.Container;
import fs16.webide.web_ide_for.container.repository.ContainerRepository;
import fs16.webide.web_ide_for.user.entity.User;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContainerService {
	private final ContainerRepository containerRepository;
	private final UserRepository userRepository; // User 엔티티 조회를 위해 필요
	private final ContainerMemberService containerMemberService; // 컨테이너 삭제 시 참여 멤버 삭제하기 위해 필요
	private final ContainerMemberRepository containerMemberRepository; // 컨테이너 생성시 참여 멤버에 추가위해 필요

	/**
	 * 사용자 ID로 사용자를 조회합니다.
	 * 
	 * @param userId 조회할 사용자 ID
	 * @return 조회된 User 엔티티
	 * @throws CoreException 해당 ID의 사용자가 존재하지 않을 경우
	 */
	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CoreException(NOT_FOUND, "ID가 " + userId + "인 사용자를 찾을 수 없습니다."));
	}

	/**
	 * 컨테이너 ID로 컨테이너를 조회합니다.
	 * 
	 * @param containerId 조회할 컨테이너 ID
	 * @return 조회된 Container 엔티티
	 * @throws CoreException 해당 ID의 컨테이너가 존재하지 않을 경우
	 */
	private Container findContainerById(Long containerId) {
		return containerRepository.findById(containerId)
			.orElseThrow(() -> new CoreException(CONTAINER_NOT_FOUND));
	}

	/**
	 * 새로운 컨테이너를 생성하고 저장합니다.
	 * * @param request 컨테이너 생성 요청 DTO
	 * @return 저장된 Container 엔티티
	 */
	@Transactional
	public Container createContainer(ContainerCreateRequest request) {
		// 1. 사용자(User) 엔티티 조회 및 검증
		User user = findUserById(request.getUser().getId());

		// 2. Container 엔티티 생성
		Container newContainer = Container.builder()
			.name(request.getName())
			.user(user) // 조회한 User 엔티티를 설정
			.build();

		// 3. Container 엔티티 저장
		Container saveContainer = containerRepository.save(newContainer);

		// 4. 컨테이너 생성 시 컨테이너 멤버에 자동 저장
		ContainerMember containerMember = new ContainerMember(user,saveContainer);
		containerMemberRepository.save(containerMember);

		return saveContainer;
	}

	/**
	 * 특정 사용자의 모든 컨테이너를 조회합니다.
	 * 
	 * @param request 컨테이너 목록 조회 요청 DTO
	 * @return 사용자가 소유한 컨테이너 목록
	 */
	@Transactional
	public List<Container> findAllContainersByUserId(ContainerListRequest request) {
		// 사용자 존재 여부 확인
		findUserById(request.getUserId());

		// 사용자 ID로 컨테이너 목록 조회
		return containerRepository.findAllByUserId(request.getUserId());
	}

	/**
	 * 특정 ID의 컨테이너를 조회합니다.
	 * 
	 * @param request 컨테이너 조회 요청 DTO
	 * @return 조회된 Container 엔티티
	 * @throws CoreException 해당 ID의 컨테이너가 존재하지 않을 경우
	 */
	@Transactional
	public Container findContainer(ContainerFindRequest request) {
		// 컨테이너 ID로 컨테이너 조회
		return findContainerById(request.getContainerId());
	}

	/**
	 * 특정 ID의 컨테이너를 삭제합니다.
	 * 
	 * @param request 컨테이너 삭제 요청 DTO
	 * @return 삭제된 Container 엔티티
	 * @throws CoreException 해당 ID의 사용자 또는 컨테이너가 존재하지 않을 경우
	 */
	@Transactional
	public Container deleteContainer(ContainerDeleteRequest request) {
		// 1. 사용자 존재 여부 확인
		findUserById(request.getUserId());

		// 2. 컨테이너 조회
		Container container = findContainerById(request.getContainerId());

		// 3. 컨테이너 삭제
		// 컨테이너에 속한 컨테이너 멤버들 삭제
		containerMemberService.removeContainerMembers(container.getId());
		containerRepository.delete(container);

		// 4. 삭제된 컨테이너 정보 반환
		return container;
	}

	/**
	 * 특정 ID의 컨테이너 이름을 수정합니다.
	 * 
	 * @param request 컨테이너 수정 요청 DTO
	 * @return 수정된 Container 엔티티
	 * @throws CoreException 해당 ID의 사용자 또는 컨테이너가 존재하지 않을 경우
	 */
	@Transactional
	public Container updateContainer(ContainerUpdateRequest request) {
		// 1. 사용자 존재 여부 확인
		findUserById(request.getUserId());

		// 2. 컨테이너 조회
		Container container = findContainerById(request.getContainerId());

		// 3. 컨테이너 이름 수정
		container.setName(request.getName());

		// 4. 수정된 컨테이너 저장 및 반환
		return containerRepository.save(container);
	}
}
