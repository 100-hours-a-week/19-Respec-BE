package kakaotech.bootcamp.respec.specranking.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerUtils implements ApplicationRunner {

    private final IPService ipService;
    private static String privateAddress;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        privateAddress = ipService.loadEC2PrivateAddress(); // 웹서버 시작 후 실행
    }

    public static String getPrivateAddress() {
        return privateAddress;
    }
}
