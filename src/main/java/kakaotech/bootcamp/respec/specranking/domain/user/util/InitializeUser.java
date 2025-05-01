package kakaotech.bootcamp.respec.specranking.domain.user.util;

import java.util.ArrayList;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class InitializeUser implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        List<User> users = new ArrayList<>();

        for (int i = 1; i <= 150; i++) {
            String loginId = "user" + i;
            String nickname = "userNickname" + i;
            String profileUrl = "https://picsum.photos/200";

            users.add(createUser(loginId, nickname, profileUrl));
        }

        userRepository.saveAll(users);
    }

    private User createUser(String loginId, String nickname, String profileUrl) {
        return new User(
                loginId,
                "1111",
                profileUrl,
                nickname,
                true
        );
    }
}
