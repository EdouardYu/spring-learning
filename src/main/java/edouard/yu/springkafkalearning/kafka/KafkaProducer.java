package edouard.yu.springkafkalearning.kafka;

import edouard.yu.springkafkalearning.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTemplate<String, UserDTO> userKafkaTemplate;
    @Value("${topic}")
    private String TOPIC ;
    @Value("${json.topic}")
    private String JSON_TOPIC ;

    public void sendMessage(String message) {
        LOGGER.info("Sending message='{}'", message);
        this.kafkaTemplate.send(TOPIC, message);
    }

    public void sendUserInformation(UserDTO user){
        Message<UserDTO> message = MessageBuilder
                .withPayload(user)
                .setHeader(KafkaHeaders.TOPIC, JSON_TOPIC)
                .build();

        LOGGER.info("Sending user information='{}'", user);
        this.userKafkaTemplate.send(message);
    }
}
