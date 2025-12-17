package fs16.webide.web_ide_for.container.service;

import static fs16.webide.web_ide_for.container.error.ContainerErrorCode.*;

import java.util.List;
import java.util.NoSuchElementException;

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

	/**
	 * 새로운 컨테이너를 생성하고 저장합니다.
	 * * @param request 컨테이너 생성 요청 DTO
	 * @return 저장된 Container 엔티티
	 */
	@Transactional
	public Container createContainer(ContainerCreateRequest request) {

		// 1. 사용자(User) 엔티티 조회 및 검증
		// ID를 사용하여 User 엔티티를 찾습니다. 없으면 예외 발생
		User user = userRepository.findById(request.getUser().getId())
			.orElseThrow(() -> new NoSuchElementException("ID가 " + request.getUser().getId() + "인 사용자를 찾을 수 없습니다."));

		// 2. Container 엔티티 생성
		// Container 엔티티의 @Builder를 사용한다고 가정하고 코드를 작성했습니다.
		Container newContainer = Container.builder()
			.name(request.getName())
			.user(user) // 조회한 User 엔티티를 설정
			// 추가 필드 설정 (Container 엔티티의 생성자에 따라 추가/수정 필요)
			.build();

		// 3. Container 엔티티 저장
		return containerRepository.save(newContainer);
	}

	/**
	 * 특정 사용자의 모든 컨테이너를 조회합니다.
	 * 
	 * @param request 컨테이너 목록 조회 요청 DTO
	 * @return 사용자가 소유한 컨테이너 목록
	 */
	@Transactional
	public List<Container> findAllContainersByUserId(ContainerListRequest request) {
		// 사용자 존재 여부 확인 (선택적)
		userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NoSuchElementException("ID가 " + request.getUserId() + "인 사용자를 찾을 수 없습니다."));

		// 사용자 ID로 컨테이너 목록 조회
		return containerRepository.findAllByUserId(request.getUserId());
	}

	/**
	 * 특정 ID의 컨테이너를 조회합니다.
	 * 
	 * @param request 컨테이너 조회 요청 DTO
	 * @return 조회된 Container 엔티티
	 * @throws NoSuchElementException 해당 ID의 컨테이너가 존재하지 않을 경우ㄱㄱ
	 */
	@Transactional
	public Container findContainer(ContainerFindRequest request) {
		// 컨테이너 ID로 컨테이너 조회
		return containerRepository.findById(request.getContainerId())
			.orElseThrow(() -> new CoreException(CONTAINER_NOT_FOUND));
	}

	/**
	 * 특정 ID의 컨테이너를 삭제합니다.
	 * 
	 * @param request 컨테이너 삭제 요청 DTO
	 * @return 삭제된 Container 엔티티
	 * @throws NoSuchElementException 해당 ID의 컨테이너가 존재하지 않을 경우
	 */
	@Transactional
	public Container deleteContainer(ContainerDeleteRequest request) {
		// 1. 사용자 존재 여부 확인
		userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NoSuchElementException("ID가 " + request.getUserId() + "인 사용자를 찾을 수 없습니다."));

		// 2. 컨테이너 조회
		Container container = containerRepository.findById(request.getContainerId())
			.orElseThrow(() -> new CoreException(CONTAINER_NOT_FOUND));

		// 3. 컨테이너 삭제
		containerRepository.delete(container);

		// 4. 삭제된 컨테이너 정보 반환
		return container;
	}

	/**
	 * 특정 ID의 컨테이너 이름을 수정합니다.
	 * 
	 * @param request 컨테이너 수정 요청 DTO
	 * @return 수정된 Container 엔티티
	 * @throws NoSuchElementException 해당 ID의 컨테이너가 존재하지 않을 경우
	 */
	@Transactional
	public Container updateContainer(ContainerUpdateRequest request) {
		// 1. 사용자 존재 여부 확인
		userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NoSuchElementException("ID가 " + request.getUserId() + "인 사용자를 찾을 수 없습니다."));

		// 2. 컨테이너 조회
		Container container = containerRepository.findById(request.getContainerId())
			.orElseThrow(() -> new CoreException(CONTAINER_NOT_FOUND));

		// 3. 컨테이너 이름 수정
		container.setName(request.getName());

		// 4. 수정된 컨테이너 저장 및 반환
		return containerRepository.save(container);
	}
}
