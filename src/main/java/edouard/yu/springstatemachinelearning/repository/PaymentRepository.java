package edouard.yu.springstatemachinelearning.repository;

import edouard.yu.springstatemachinelearning.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
