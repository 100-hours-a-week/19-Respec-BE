package kakaotech.bootcamp.respec.specranking.domain.user.util;

import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    private static Boolean mockUserEnabled;

    public void setFakeUserIdEnabled(@Value("${mock.login.user}") Boolean enabled) {
        mockUserEnabled = enabled;
    }

    public static Long getCurrentUserId() {
        if (mockUserEnabled) {
            return 1L;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserDto userDto)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        return userDto.getId();
    }
}
