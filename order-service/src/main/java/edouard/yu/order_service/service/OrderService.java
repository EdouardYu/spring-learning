package edouard.yu.order_service.service;

import edouard.yu.base_domains.dto.Order;
import edouard.yu.base_domains.dto.OrderEvent;
import edouard.yu.base_domains.dto.enumeration.OrderStatus;
import edouard.yu.order_service.kafka.OrderProducer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderProducer orderProducer;

    public void placeOrder(Order order){
        OrderEvent orderEvent = OrderEvent.builder()
                .message("Order placed")
                .status(OrderStatus.PENDING)
                .order(order)
                .build();

        this.orderProducer.sendMessage(orderEvent);
    }
}
