package kakaotech.bootcamp.respec.specranking.domain.user.util;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

public class UserUtils {

    private static GetCurrentUserStrategy strategy;

    @Component
    public static class StrategyInjector {
        public StrategyInjector(GetCurrentUserStrategy currentStrategy) {
            UserUtils.strategy = currentStrategy;
        }
    }

    public static Long getCurrentUserId() {
        if (strategy == null) {
            throw new IllegalStateException("사용자 정보 조회 전략이 초기화되지 않았습니다.");
        }
        return strategy.getUserId();
    }

    public interface GetCurrentUserStrategy {
        Long getUserId();
    }

    @Component
    @Profile("no-auth")
    public static class MockStrategy implements GetCurrentUserStrategy {
        @Override
        public Long getUserId() {
            return 1L;
        }
    }
}