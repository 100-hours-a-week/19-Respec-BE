package kakaotech.bootcamp.respec.specranking.global.devsetup;

import java.util.ArrayList;
import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.user.entity.User;
import kakaotech.bootcamp.respec.specranking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("user-initialize")
@Order(1)
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

        System.out.println("150명의 사용자 데이터가 성공적으로 생성되었습니다.");
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
