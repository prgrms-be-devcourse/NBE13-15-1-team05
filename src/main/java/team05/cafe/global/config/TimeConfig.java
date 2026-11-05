package team05.cafe.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
/**
 * 서울 시각
 * LocalDateTime.now(clock)으로 서울 시각을 반환 받을 수 있습니다
 */

@Configuration
public class TimeConfig {

    public static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    @Bean
    public Clock clock() {
        return Clock.tickSeconds(SEOUL);
    }
}
