package fs16.webide.web_ide_for.user.service;

import com.github.benmanes.caffeine.cache.Cache;
import fs16.webide.web_ide_for.common.error.CoreException;
import fs16.webide.web_ide_for.container_member.repository.ContainerMemberRepository;
import fs16.webide.web_ide_for.user.dto.UserInfoResponse;
import fs16.webide.web_ide_for.user.dto.UserSearchResponse;
import fs16.webide.web_ide_for.user.entity.User;
import fs16.webide.web_ide_for.user.error.UserErrorCode;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ContainerMemberRepository containerMemberRepository;
    private final Cache<Long, UserInfoResponse> userCache;

    // 회원가입 및 로그인
    public void findOrCreateUser(String clerkUserId, Map<String,Object> claims) {

        // 로그인
        Optional<User> optionalUser = userRepository.findByClerkId(clerkUserId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(!"Y".equals(user.getStatus())){
                throw new CoreException(UserErrorCode.USER_NOT_FOUND);
            }

            log.info("기존 회원 로그인 : {}",clerkUserId);
            return;
        }


        // 회원가입
        Object verifiedObj = claims.get("email_verified");
        boolean isEmailVerified = verifiedObj instanceof Boolean b ? b
                : verifiedObj instanceof String s && Boolean.parseBoolean(s);

        // 이메일 인증된 회원만 DB 저장
        if (!isEmailVerified) {
            log.info("이메일 미인증 clerkId={}", clerkUserId);
            throw new CoreException(UserErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 이름 / 이미지
        String firstName = Optional.ofNullable(claims.get("first_name"))
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse("");

        String lastName = Optional.ofNullable(claims.get("last_name"))
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse("");
        String name = (lastName + firstName).trim();

        if (name.isBlank() || name.equalsIgnoreCase("null")) {
            name = "사용자";
        }
        String profileImageUrl = claims.getOrDefault("image_url", "").toString();


        // 신규 생성
        User newUser = new User();
        newUser.setClerkId(clerkUserId);
        newUser.setName(name);
        newUser.setProfileImageUrl(profileImageUrl);
        newUser.setStatus("Y");

        userRepository.save(newUser);
        log.info("새 유저 생성 완료 clerkId={}", clerkUserId);
    }

    // webhook - 유저 업데이트(이름 변경 / 프로필 변경)
    public void updateUser(Long userId,Map<String,Object> data) {
        log.info("====updateUser=====");
        String firstName = Optional.ofNullable(data.get("first_name"))
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse("");

        String lastName = Optional.ofNullable(data.get("last_name"))
                .map(Object::toString)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElse("");

        String name = (lastName + firstName).trim();

        if (name.isBlank() || name.equalsIgnoreCase("null")) {
            name = "사용자";
        }
        String profileImageUrl = data.getOrDefault("profile_image_url","").toString();

        boolean changed = false;
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty()){
            log.info("userId 없음");
            return;
        }

        User user = optionalUser.get();

        if(!"Y".equals(user.getStatus())) {
            log.info("탈퇴한 회원");
            return;
        }

        // 변경된 데이터가 있을시에만 변경
        if(!Objects.equals(user.getName(),name)){
            changed = true;
            user.setName(name);
        }

        if(!Objects.equals(user.getProfileImageUrl(),profileImageUrl)){
            changed = true;
            user.setProfileImageUrl(profileImageUrl);
        }

        if(changed){
            userRepository.save(user);
            userCache.invalidate(userId);
        }
    }

    @Transactional
    // webhook - 유저 탈퇴(status = N)
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.setStatus("N");  // 삭제 대신 상태 변경
                    userRepository.save(user);
                    containerMemberRepository.deleteAllByUser(user);
                    userCache.invalidate(userId);
                    log.info("유저 탈퇴 처리 완료 status='N' userId={}", userId);
                });
    }

   // 컨테이너 초대 유저 검색 (이름, clerkId)
    public List<UserSearchResponse> findUsers(Long containerId, String keyword){
        List<User> users = userRepository.searchNameOrClerkId(keyword);

        Set<Long> invitedUserIds = new HashSet<>(
                containerMemberRepository.findUserIdsByContainerId(containerId)
        );

        return users.stream()
                .map(user -> new UserSearchResponse(
                        user.getId(),
                        user.getClerkId(),
                        user.getName(),
                        user.getProfileImageUrl(),
                        invitedUserIds.contains(user.getId())
                )).toList();
    }

    public Long getUserIdByClerkId(String clerkUserId){
        return userRepository.findByClerkId(clerkUserId)
                .map(User::getId).orElseThrow(()->new CoreException(UserErrorCode.USER_NOT_FOUND));
    }


    // 유저 기본 정보 조회(WebSocket / Chat 공용)
    public UserInfoResponse getUserInfo(Long userId) {
        return userCache.get(userId, id -> {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));

            if (!"Y".equals(user.getStatus())) {
                throw new CoreException(UserErrorCode.USER_NOT_FOUND);
            }

            return new UserInfoResponse(
                    user.getName(),
                    user.getProfileImageUrl()
            );
        });
    }

    //  WebSocket CONNECT 시 캐시 워밍업
    public void preloadUserInfo(Long userId) {
        getUserInfo(userId);
    }

    // 유저 반환
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CoreException(UserErrorCode.USER_NOT_FOUND));
    }
}
