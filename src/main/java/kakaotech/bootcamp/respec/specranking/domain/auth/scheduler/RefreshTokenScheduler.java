package kakaotech.bootcamp.respec.specranking.domain.auth.scheduler;

import kakaotech.bootcamp.respec.specranking.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenScheduler {

    private final AuthService authService;

    // 1시간마다 실행
    @Scheduled(fixedRate = 3600000)
    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        log.info("Deleting expired tokens");
        authService.deleteByExpirationBefore(now);
        log.info("Expired tokens deleted.");
    }
}
