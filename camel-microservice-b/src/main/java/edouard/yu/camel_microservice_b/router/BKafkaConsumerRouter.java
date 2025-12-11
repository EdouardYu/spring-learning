package edouard.yu.camel_microservice_b.router;

import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class BKafkaConsumerRouter extends RouteBuilder {
    @Override
    public void configure() {
        from("kafka:kafka-topic")
                .log("Message reçu de Kafka : ${body}");

    }
}

