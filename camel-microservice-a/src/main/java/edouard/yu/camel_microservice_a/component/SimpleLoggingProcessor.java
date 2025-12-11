package edouard.yu.camel_microservice_a.component;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLoggingProcessor implements Processor {
    private final Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessor.class);

    @Override
    public void process(Exchange exchange) {
        logger.info("Processing message {} by processor", exchange.getMessage().getBody());
    }
}
