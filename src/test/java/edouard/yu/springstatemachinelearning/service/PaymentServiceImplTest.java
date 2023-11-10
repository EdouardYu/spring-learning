package edouard.yu.springstatemachinelearning.service;

import edouard.yu.springstatemachinelearning.domain.PaymentEvent;
import edouard.yu.springstatemachinelearning.domain.PaymentState;
import edouard.yu.springstatemachinelearning.entity.Payment;
import edouard.yu.springstatemachinelearning.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        this.payment = Payment.builder().amount(new BigDecimal("12.99")).build();
    }

    @Test
    void preAuth() {
        Payment savedPayment = this.paymentService.newPayment(this.payment);
        assertEquals(savedPayment.getState(), PaymentState.NEW);
        log.info(savedPayment.toString());

        StateMachine<PaymentState, PaymentEvent> sm = this.paymentService.preAuth(savedPayment.getId());

        Payment preAuthedPayment = this.paymentRepository.findById(savedPayment.getId()).orElseThrow(() -> new RuntimeException("Payment not found"));

        assertEquals(preAuthedPayment.getState(), sm.getState().getId());
        PaymentState[] preAuthStates = { PaymentState.PRE_AUTH, PaymentState.PRE_AUTH_ERROR };
        assertTrue(Arrays.asList(preAuthStates).contains(preAuthedPayment.getState()));
        log.info(preAuthedPayment.toString());
    }

    @RepeatedTest(10)
    void authorize() {
        Payment savedPayment = this.paymentService.newPayment(this.payment);

        StateMachine<PaymentState, PaymentEvent> preAuthSM = this.paymentService.preAuth(savedPayment.getId());

        if(preAuthSM.getState().getId() == PaymentState.PRE_AUTH) {
            assertEquals(preAuthSM.getState().getId(), PaymentState.PRE_AUTH);
            log.info("Payment is pre-authorized");
            StateMachine<PaymentState, PaymentEvent> authSM = this.paymentService.authorize(savedPayment.getId());

            PaymentState[] authStates = { PaymentState.AUTH, PaymentState.AUTH_ERROR };
            assertTrue(Arrays.asList(authStates).contains(authSM.getState().getId()));
            log.info("Result of Auth: " + authSM.getState().getId());
        } else {
            assertEquals(preAuthSM.getState().getId(), PaymentState.PRE_AUTH_ERROR);
            log.info("Payment failed pre-authorized...");
        }
    }
}