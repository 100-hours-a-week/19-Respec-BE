package kakaotech.bootcamp.respec.specranking.global.util;

import org.springframework.stereotype.Service;

@Service
public class MockGetCurrentUserService implements GetCurrentUserService {
    @Override
    public Long getUserId() {
        return 1L;
    }
}
