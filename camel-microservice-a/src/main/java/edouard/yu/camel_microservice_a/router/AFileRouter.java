package edouard.yu.camel_microservice_a.router;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class AFileRouter extends RouteBuilder {

    @Override
    public void configure() {
        from("file:files/input")
                .log("${body}")
                .to("file:files/output");
    }
}

