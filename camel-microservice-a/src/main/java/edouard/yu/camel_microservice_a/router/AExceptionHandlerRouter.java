package edouard.yu.camel_microservice_a.router;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AExceptionHandlerRouter extends RouteBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AExceptionHandlerRouter.class);

    @Override
    public void configure() {

        onException(RuntimeException.class)
                .redeliveryDelay(1000) // reessaye de faire le routeur 5 fois toute les secondes avant de balancer l'erreur
                .maximumRedeliveries(5)
                .log("Erreur dans le routeur A : ${exception.message}")
                .handled(true); // permet de signifier que l'erreur a ete resolu donc n'affiche pas de message d'erreur dans le log

        onException(Exception.class)
                .log("Erreur generique dans le routeur A : ${exception.message}");


        from("direct://AExceptionHandlerRouter")
                .process(exchange -> {
                    String channel = exchange.getIn().getHeader("channelName").toString();
                    LOGGER.info("Header channel = {}", channel);
                    throw new RuntimeException("Erreur dans le routeur A");})
                .end();
    }
}
