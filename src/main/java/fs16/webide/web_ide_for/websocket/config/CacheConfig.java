package fs16.webide.web_ide_for.websocket.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import fs16.webide.web_ide_for.user.dto.UserInfoResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<Long, UserInfoResponse> userCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES) // 30분 캐시
                .maximumSize(1000)
                .build();
    }
}
