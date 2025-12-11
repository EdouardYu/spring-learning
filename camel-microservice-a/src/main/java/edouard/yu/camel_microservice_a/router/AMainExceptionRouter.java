package edouard.yu.camel_microservice_a.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AMainExceptionRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://mainRoute?repeatCount=1") // ne lance qu'une seule fois
                .setHeader("channelName", constant("spring-apache-camel-channel"))
                .to("direct://AExceptionHandlerRouter");
    }
}
