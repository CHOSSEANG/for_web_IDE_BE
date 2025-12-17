package fs16.webide.web_ide_for.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "User", description = "로그인/회원가입 API")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Operation(summary = "로그인/회원가입", description = "jwt 유효검사를 통해 신규 생성 및 조회를 합니다")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok("로그인 성공");
    }




}
