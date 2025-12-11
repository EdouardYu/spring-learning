package edouard.yu.camel_microservice_a.router;

import edouard.yu.camel_microservice_a.component.GetCurrentTimeBean;
import edouard.yu.camel_microservice_a.component.SimpleLoggingProcessingComponent;
import edouard.yu.camel_microservice_a.component.SimpleLoggingProcessor;
import lombok.AllArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ATimerRouter  extends RouteBuilder {
    private final GetCurrentTimeBean getCurrentTimeBean;
    private final SimpleLoggingProcessingComponent loggingComponent;

    @Override
    public void configure() {
        // timer
        // transformation
        // log
        // .choice().when(new Predicate() {}).to().otherwise().to().endChoice()
        //.body()
        //.routeId("id-route") permet de donner un id
        //onExeption
        // on peut egalement manipuler des bdd en sql pure ou jdbc ou jpa
        // faire des api restfull, des SEDA (systeme de queue distribuer), etc.
        // on peu aussi faire des tests unitaires

        // first-timer est le nom du timer et le tout est un endpoint
        from("timer:first-timer")
                .log("${body}") // renvoie a valeur du flux de l'instant actuel
                .transform().simple("A simple text")
                //.transform().constant("Time now is " + LocalDateTime.now())
                .log("${body}")
                .bean(this.getCurrentTimeBean, "getCurrentTime") // on doit specifier obligatoirement la methode s'il y a plusieurs methodes dans le bean
                .bean(this.loggingComponent, "process")
                .process(new SimpleLoggingProcessor())
                .to("log:first-timer"); // c'est seulement la derniere transformation qui en ressort
    }
}

