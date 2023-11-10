package edouard.yu.springstatemachinelearning.entity;

import edouard.yu.springstatemachinelearning.domain.PaymentState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data // Ensemble : @ToString, @EqualsAndHashCode, @Getter, @Setter et @RequiredArgsConstructor
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private long id;
    @Enumerated(EnumType.STRING)
    private PaymentState state;
    private BigDecimal amount;
}
