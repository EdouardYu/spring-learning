package edouard.yu.springkafkalearning.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${topic}")
    private String TOPIC ;
    @Value("${json.topic}")
    private String JSON_TOPIC ;

    @Bean
    public NewTopic springKafkaLearningTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic springKafkaLearningJsonTopic() {
        return TopicBuilder.name(JSON_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
