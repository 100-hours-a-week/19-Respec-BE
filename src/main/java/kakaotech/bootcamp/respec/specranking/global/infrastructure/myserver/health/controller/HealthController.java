package kakaotech.bootcamp.respec.specranking.global.infrastructure.myserver.health.controller;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.myserver.health.constant.HealthConstant.health_RETURN_MESSAGE;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public String health() {
        return health_RETURN_MESSAGE;
    }
}
