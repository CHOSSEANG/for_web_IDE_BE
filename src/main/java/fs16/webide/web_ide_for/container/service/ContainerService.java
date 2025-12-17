package fs16.webide.web_ide_for.container.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import fs16.webide.web_ide_for.container.dto.ContainerCreateRequest;
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
		User user = userRepository.findById(request.getUserId())
			.orElseThrow(() -> new NoSuchElementException("ID가 " + request.getUserId() + "인 사용자를 찾을 수 없습니다."));

		// 2. Container 엔티티 생성
		// Container 엔티티의 @Builder를 사용한다고 가정하고 코드를 작성했습니다.
		Container newContainer = Container.builder()
			.name(request.getName())
			.user(user) // 조회한 User 엔티티를 설정
			// 추가 필드 설정 (Container 엔티티의 생성자에 따라 추가/수정 필요)
			// .imageTag(request.getImageTag()) // 만약 Container 엔티티에 imageTag 필드가 있다면 설정
			.build();

		// 3. Container 엔티티 저장
		return containerRepository.save(newContainer);
	}
}
