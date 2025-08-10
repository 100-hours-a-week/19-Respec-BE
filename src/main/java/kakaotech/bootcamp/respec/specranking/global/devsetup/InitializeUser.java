package kakaotech.bootcamp.respec.specranking.global.devsetup;

import java.util.ArrayList;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("user-initialize")
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class InitializeUser implements CommandLineRunner {

    private final UserRepository userRepository;

    private static final int USER_COUNT = 150;
    private static final String DEFAULT_PASSWORD = "1111";
    private static final String DEFAULT_PROFILE_URL = "https://picsum.photos/200";

    @Override
    public void run(String... args) {
        List<User> users = generateUsers(USER_COUNT);
        userRepository.saveAll(users);
        log.info("{}명의 사용자 데이터가 성공적으로 생성되었습니다.", USER_COUNT);
    }

    private List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String loginId = "user" + i;
            String nickname = "userNickname" + i;
            users.add(createUser(loginId, nickname));
        }
        return users;
    }

    private User createUser(String loginId, String nickname) {
        return new User(
                loginId,
                DEFAULT_PASSWORD,
                DEFAULT_PROFILE_URL,
                nickname,
                true
        );
    }
}
