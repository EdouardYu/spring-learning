package edouard.yu.base_domains.dto;

import edouard.yu.base_domains.dto.enumeration.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private String message;
    private OrderStatus status;
    private Order order;
}
