package edouard.yu.springkafkalearning.kafka;

import edouard.yu.springkafkalearning.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "${topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        LOGGER.info("Received message='{}'", message);
    }

    @KafkaListener(topics = "${json.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeJson(UserDTO user) {
        LOGGER.info("Received user ifo='{}'", user);
    }
}
