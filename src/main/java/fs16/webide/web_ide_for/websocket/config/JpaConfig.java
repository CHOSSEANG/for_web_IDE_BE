package fs16.webide.web_ide_for.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Configuration
public class JpaConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        // OffsetDateTime을 그대로 사용해서 createdAt에 들어가도록 설정
        return () -> Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }
}