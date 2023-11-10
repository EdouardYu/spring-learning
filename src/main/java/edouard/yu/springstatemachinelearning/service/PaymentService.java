package edouard.yu.springstatemachinelearning.service;

import edouard.yu.springstatemachinelearning.domain.PaymentEvent;
import edouard.yu.springstatemachinelearning.domain.PaymentState;
import edouard.yu.springstatemachinelearning.entity.Payment;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
    Payment newPayment(Payment payment);

    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);

    StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);
}
