package edouard.yu.springstatemachinelearning.config;

import edouard.yu.springstatemachinelearning.domain.PaymentEvent;
import edouard.yu.springstatemachinelearning.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StateMachineConfigTest {
    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
        // reactor-core :
        sm.startReactively().block(); // startReactively renvoie un mono
        // block : asynchrone bloquant, subscribe à ce Mono et bloque indéfiniment le thread principal jusqu'à ce qu'un prochain signal soit reçu.
        // À la différence de la méthode subscribe qui est asynchrone non-bloquant
        assertEquals(sm.getState().getId(), PaymentState.NEW);

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE).build())).blockLast(); // sendEvent renvoie un flux
        // blockLast : asynchrone bloquant, subscribe à ce Flux et bloque indéfiniment le thread principal jusqu'à ce que le upstream signale sa dernière valeur ou se termine.
        PaymentState[] preAuthStates = { PaymentState.PRE_AUTH, PaymentState.PRE_AUTH_ERROR };
        assertTrue(Arrays.asList(preAuthStates).contains(sm.getState().getId()));

        // Différence :
        // Mono : flux pouvant en émettre un seul élément au maximum, ayant donc une cardinalité de 0 à 1.
        // Flux : flux pouvant en émettre une infinité d'éléments, ayant une cardinalité de 0 à N.

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).build())).blockLast();
        assertEquals(sm.getState().getId(), PaymentState.PRE_AUTH);

        // rem : si on teste un évènement qui n'a pas été configuré dans la factory,
        //       la State Machine va ignorer l'évènement et ne pas renvoyer d'erreur
    }
}