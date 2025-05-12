package kakaotech.bootcamp.respec.specranking.domain.user.util;

import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    private static Boolean mockUserEnabled;

    public UserUtils(@Value("${mock.login.user}") Boolean mockUserEnabled) {
        UserUtils.mockUserEnabled = mockUserEnabled;
    }

    public static Optional<Long> getCurrentUserId() {
        if (mockUserEnabled) {
            return Optional.of(1L);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserDto userDto)) {
            return Optional.empty();
        }

        return Optional.of(userDto.getId());
    }
}
