package edouard.yu.camel_microservice_a.router;

import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AKafkaProducerRouter extends RouteBuilder {
    @Override
    public void configure() {
        from("timer:kafka-timer?period=5000") // 5 secondes
                .transform().constant("Message envoyé via Camel Kafka")
                .log("${body}")
                .to("kafka:kafka-topic");
    }
}

