package kakaotech.bootcamp.respec.specranking.domain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class OAuth2RedisConfig {

    // OAuth2AuthorizationRequest 전용 RedisTemplate
    // OAuth2AuthorizationRequest의 복잡한 객체 구조를 처리하기 위해 Jackson 아닌 JdkSerializationRedisSerializer 사용함

    @Bean
    public RedisTemplate<String, OAuth2AuthorizationRequest> oauth2AuthorizationRequestRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, OAuth2AuthorizationRequest> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jdkSerializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jdkSerializer);

        return template;
    }
}
