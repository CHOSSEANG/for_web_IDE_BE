package fs16.webide.web_ide_for.user.controller;

import fs16.webide.web_ide_for.user.dto.UserSearchResponse;
import fs16.webide.web_ide_for.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "User", description = "User 컨트롤러")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "로그인/회원가입", description = "jwt 유효검사를 통해 신규 생성 및 조회를 합니다")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("로그인 성공");
    }


    @Operation(summary = "유저 검색", description = "초대할 유저를 키워드로 검색합니다")
    @GetMapping("/search")
    public List<UserSearchResponse> findUsers(@RequestParam Long containerId ,@RequestParam String keyword) {
        return userService.findUsers(containerId,keyword);
    }



}
