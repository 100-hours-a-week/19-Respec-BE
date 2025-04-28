package kakaotech.bootcamp.respec.specranking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpecRankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpecRankingApplication.class, args);
    }

}
