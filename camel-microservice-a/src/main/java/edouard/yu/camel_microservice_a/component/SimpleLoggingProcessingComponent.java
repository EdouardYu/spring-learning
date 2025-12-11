package edouard.yu.camel_microservice_a.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SimpleLoggingProcessingComponent {
    private final Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);
    public void process(String message) {
       logger.info("Processing message {} by component", message);
    }
}
