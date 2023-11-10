package edouard.yu.springstatemachinelearning.service;

import edouard.yu.springstatemachinelearning.domain.PaymentEvent;
import edouard.yu.springstatemachinelearning.domain.PaymentState;
import edouard.yu.springstatemachinelearning.entity.Payment;
import edouard.yu.springstatemachinelearning.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return this.paymentRepository.save(payment);
    }

    //@Transactional // permet d'encapsuler la méthode dans un contexte transactionnel si on utilise Spring Data JPA en mode lazy loading.
    // À cause des getOne (dépréciés) ou des find en mode lazy loading, nous risquerons de travailler en dehors d'un contexte transactionnel
    // Si nous n'avons pas spécifié cette annotation, la transaction getOne, etc. n'auront pas de fin et peut causer des LazyInitializationException
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.PRE_AUTHORIZE);
        return sm;
    }

    //@Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId, sm, PaymentEvent.AUTHORIZE);
        return sm;
    }

    // Cette méthode permet de restaurer (restore) la State Machine à partir de la bdd
    // dans le cas d'utilisation où nous aurons des évènements séparés qui doivent tous être exécutés l'un après l'autre,
    // mais sur une échelle de temps indéterminé, on ne va pas laisser continuellement faire tourner la machine jusqu'à que toutes les étapes soient effectuées.
    // D'une part celà consommerait beaucoup de ressources inutilement, d'autre part, on risque de perdre l'avancement du process si la machine s'éteint.
    // À la place, après chaque évènement, nous devons conserver (persister) l'état de la machine dans la bdd, puis quand il aura un nouvel évènement,
    // nous allons (réinitialiser (reset) + refresh dans le bon état) = restaurer la State Machine à partir de l'objet stocké la bdd.
    // Même si c'est un processus coûteux, elle reste sans doute moins couteuse et risquée que la première option
    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = this.paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
        // On initialise une State Machine avec l'identifiant du paiement figée dans la bdd
        StateMachine<PaymentState, PaymentEvent> sm = this.stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        // reactor-core :
        sm.stopReactively().block(); // nous devons lui dire d'arrêter pour qu'il ne soit pas actif
        // startReactively, stopReactively et resetStateMachineReactively renvoie un mono
        // block : asynchrone bloquant, subscribe à ce Mono et bloque indéfiniment le thread principal jusqu'à ce qu'un prochain signal soit reçu.
        // À la différence de la méthode subscribe qui est asynchrone non-bloquant

        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(this.paymentStateChangeInterceptor); // On initialise l'itérateur pour que la State Machine sache quoi faire à chaque fois qu'il aurait un évènement
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(
                            payment.getState(), // nous réglons la State Machine du paiement sur son état spécifique figé dans la bdd
                            null,
                            null,
                            null
                    )).block();
                });

        sm.startReactively().block(); // nous redémarrons la State Machine pour qu'il soit de nouveau actif et avec le bon state

        return sm;
    }

    // Une des fonctionnalités intéressante de la State Machine est qu'elle en charge l'infrastructure des messages spring
    // On va donc envoyer à la State Machine, un message spring standard qui va contenir toutes les informations à propos de l'évènement
    // et cette méthode permettant d'envoyer des évènements dans la State Machine.
    private void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent event) {
         Message<PaymentEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId) // On enrichit le message avec notamment ici, l'id du paiement qu'on passe dans le header
                .build();

         sm.sendEvent(Mono.just(msg)).blockLast();// sendEvent renvoie un flux
        // blockLast : asynchrone bloquant, subscribe à ce Flux et bloque indéfiniment le thread principal jusqu'à ce que le upstream signale sa dernière valeur ou se termine.

        // Différence :
        // Mono : flux pouvant en émettre un seul élément au maximum, ayant donc une cardinalité de 0 à 1.
        // Flux : flux pouvant en émettre une infinité d'éléments, ayant une cardinalité de 0 à N.
    }
}
