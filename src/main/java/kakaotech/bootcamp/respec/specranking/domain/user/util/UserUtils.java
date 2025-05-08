package kakaotech.bootcamp.respec.specranking.domain.user.util;

import kakaotech.bootcamp.respec.specranking.domain.auth.dto.AuthenticatedUserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtils {

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUserDto userDto)) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        return userDto.getId();
    }
}
