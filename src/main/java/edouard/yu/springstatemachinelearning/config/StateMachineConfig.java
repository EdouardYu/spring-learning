package edouard.yu.springstatemachinelearning.config;

import edouard.yu.springstatemachinelearning.domain.PaymentEvent;
import edouard.yu.springstatemachinelearning.domain.PaymentState;
import edouard.yu.springstatemachinelearning.service.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.Random;

@Slf4j
@EnableStateMachineFactory // permet d'activer la State Machine Factory, on aura alors un composant pour générer une State Machine
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> { // permet de configurer les états de la State Machine
    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW) // On indique tous les états initiaux de la State Machine
                .states(EnumSet.allOf(PaymentState.class)) // permet d'obtenir et de charger, dans la State Machine, la liste d'énumération de PaymentState
                .end(PaymentState.AUTH) // On indique tous les états terminaux de la State Machine
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal() // permet de dire que c'est une configuration externe
                .source(PaymentState.NEW) // On indique l'état initial de la State Machine avant l'événement
                .target(PaymentState.NEW) // On indique l'état final de la State Machine après l'événement
                .event(PaymentEvent.PRE_AUTHORIZE) // L'évènement en question
                .action(preAuthAction()) // on définit l'action pour tous les évènements pré-authorize, ici, l'action preAuthAction est effectué
                .and() // permet d'ajouter d'autres évènements qui provoquent ou non des changements d'états
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE)
                .action(authAction())
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            // Après chaque évènement, on exécute cette fonction
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from, to)); // Après chaque évènement, on affiche ce log
            }
        };

        config.withConfiguration().listener(adapter); // Permet de configurer le listener des évènements
    }

    // Pour tous les évènements pré-authorize, dans cette action, on appelle un nouvel évènement pour soit accepter, soit refuser la pré-autorisation
    // Les actions sont des logiques métiers polyvalents et sont utiles pour envoyer des messages à un autre système, appeler un web service, appeler la bdd dans certains cas
    public Action<PaymentState, PaymentEvent> preAuthAction() {
        return stateContext -> {
            log.info("PreAuth was called!!");
            if (new Random().nextInt(10) < 8) { // Dans 80% des cas la pré-autorisation est acceptée et 20% des cas, elle est refusée
                log.info("PreAuth Approved");
                stateContext.getStateMachine().sendEvent(Mono.just(
                        MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build()
                )).blockLast();
            } else {
                log.info("PreAuth Declined! No credit!!");
                stateContext.getStateMachine().sendEvent(Mono.just(
                        MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build()
                )).blockLast();
            }
        };
    }

    public Action<PaymentState, PaymentEvent> authAction() {
        return stateContext -> {
            log.info("Auth was called!!");
            if (new Random().nextInt(10) < 8) { // Dans 80% des cas la pré-autorisation est acceptée et 20% des cas, elle est refusée
                log.info("Auth Approved");
                stateContext.getStateMachine().sendEvent(Mono.just(
                        MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build()
                )).blockLast();
            } else {
                log.info("Auth Declined! No credit!!");
                stateContext.getStateMachine().sendEvent(Mono.just(
                        MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                                .build()
                )).blockLast();
            }
        };
    }
}
