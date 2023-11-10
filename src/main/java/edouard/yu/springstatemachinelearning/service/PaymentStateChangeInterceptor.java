package edouard.yu.springstatemachinelearning.service;

import edouard.yu.springstatemachinelearning.domain.PaymentEvent;
import edouard.yu.springstatemachinelearning.domain.PaymentState;
import edouard.yu.springstatemachinelearning.entity.Payment;
import edouard.yu.springstatemachinelearning.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Cette classe représente l'intercepteur de la State Machine, les méthodes à l'intérieur servent à réagir
// et définir ce qu'on fait pour chaque évènement
@RequiredArgsConstructor
@Component // Annotation générique qui est hérité par @Service et toutes les autres annotations qui permettent de créer des beans sur Spring
// Un bean est une méthode qu'on peut instancier
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepository paymentRepository;

    // Cette méthode est appelé avant chaque transition d'état de la State Machine
    // Et ici, on s'en sert pour faire la persistence de l'état de la State Machine dans la bdd
    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state,
                               Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine,
                               StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        //Si nous trouvons l'id dans le header du message et que l'id est présent dans la bdd, nous lui enregistrons son nouvel état
        Optional.ofNullable((Long) message.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, -1L)) // la valeur par défaut est -1 sous forme de long (L)
                .ifPresent(paymentId -> {
                    Payment payment = this.paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
                    payment.setState(state.getId());
                    this.paymentRepository.save(payment);
                });
    }
}
