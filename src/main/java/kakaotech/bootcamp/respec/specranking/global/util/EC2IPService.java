package kakaotech.bootcamp.respec.specranking.global.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Profile("!local")
@RequiredArgsConstructor
public class EC2IPService implements IPService {

    private final WebClient ec2MetadataWebClient;
    private final ServletWebServerApplicationContext webServerContext;

    public String loadEC2PrivateAddress() {
        return fetchMetadata("local-ipv4") + ":" + getPrivatePort();
    }

    private String fetchMetadata(String path) {
        return ec2MetadataWebClient
                .get()
                .uri("/" + path)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(3))
                .block();
    }

    private int getPrivatePort() {
        return webServerContext.getWebServer().getPort();
    }
}
