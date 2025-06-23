package kakaotech.bootcamp.respec.specranking.global.infrastructure.kafka.config;

import java.util.HashMap;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.produce.ChatProduceDto;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.chat.topic.partitions}")
    private int partitions_cnt;

    @Value("${spring.kafka.chat.topic.replicas}")
    private short replicas_cnt;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, ChatProduceDto> chatMessageProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ChatProduceDto> chatMessageKafkaTemplate() {
        return new KafkaTemplate<>(chatMessageProducerFactory());
    }

    @Bean
    public NewTopic chatTopic() {
        return TopicBuilder.name("chat")
                .partitions(partitions_cnt)
                .replicas(replicas_cnt)
                .build();
    }

}
