package fs16.webide.web_ide_for.clerk;

import fs16.webide.web_ide_for.clerk.service.ClerkJwtService;
import fs16.webide.web_ide_for.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ClerkJwtService clerkJwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(ClerkJwtService clerkJwtService, UserService userService) {
        this.clerkJwtService = clerkJwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // 1. JWT 검증
                Map<String, Object> claims = clerkJwtService.validate(token);
                String clerkUserId = claims.get("sub").toString();
                // 2. DB에서 유저 확인, 없으면 생성 (로그인 시점)
                userService.findOrCreateUser(clerkUserId, claims);

                // 3. 인증 세팅만 수행
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(clerkUserId, null, null);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            } catch (Exception e) {
                log.error("JWT 인증/유저 처리 실패", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}

