package kakaotech.bootcamp.respec.specranking.global.infrastructure.myserver.ip;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!local")
@RequiredArgsConstructor
public class EC2IPService implements IPService {

    @Value("${server.private-address}")
    private String serverPrivateAddress;

    public String loadEC2PrivateAddress() {
        return serverPrivateAddress;
    }

}
