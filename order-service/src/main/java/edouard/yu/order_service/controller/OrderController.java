package edouard.yu.order_service.controller;

import edouard.yu.base_domains.dto.Order;
import edouard.yu.order_service.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
@AllArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @ResponseStatus(value = HttpStatus.OK)
    @PostMapping()
    public void placeOrder(@RequestBody Order order){
        this.orderService.placeOrder(order);
    }
}
