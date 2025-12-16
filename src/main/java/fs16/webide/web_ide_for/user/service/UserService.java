package fs16.webide.web_ide_for.user.service;

import fs16.webide.web_ide_for.user.entity.User;
import fs16.webide.web_ide_for.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // 1.회원가입 및 로그인
    public void findOrCreateUser(String clerkUserId, Map<String,Object> claims) {

        // 로그인
        Optional<User> optionalUser = userRepository.findByClerkId(clerkUserId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if(!"Y".equals(user.getStatus())){
                throw new IllegalStateException("탈퇴한 회원");
//                throw new CoreException(CommonErrorCode.BAD_REQUEST, "탈퇴한 회원입니다.");
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
            throw new IllegalStateException("이메일 미인증");
        }

        // 이름 / 이미지
        String firstName = claims.getOrDefault("first_name", "").toString();
        String lastName = claims.getOrDefault("last_name", "").toString();
        String name = (lastName + firstName).trim();
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

    // 2. webhook - 유저 업데이트(이름 변경 / 프로필 변경)
    public void updateUser(String clerkUserId,Map<String,Object> data) {

        String firstName = data.getOrDefault("first_name","").toString();
        String lastName = data.getOrDefault("last_name","").toString();
        String name = (lastName + firstName).trim();
        String profileImageUrl = data.getOrDefault("profile_image_url","").toString();

        boolean changed = false;
        Optional<User> optionalUser = userRepository.findByClerkId(clerkUserId);

        if(optionalUser.isEmpty()){
            log.info("clerkUserId 없음");
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
        }
    }

    // 3. webhook - 유저 탈퇴(status = N)
    public void deleteUser(String clerkUserId) {
        userRepository.findByClerkId(clerkUserId)
                .ifPresent(user -> {
                    user.setStatus("N");  // 삭제 대신 상태 변경
                    userRepository.save(user);
                    log.info("유저 탈퇴 처리 완료 status='N' clerkId={}", clerkUserId);
                });
    }


}
