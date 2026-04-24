package br.com.gustavo.order.service.handler;

import br.com.gustavo.order.service.dto.OrderEvent;
import org.springframework.stereotype.Component;

@Component
public class HandleDeliveringOrderEvent implements HandleOrderEvent {

    @Override
    public void process(OrderEvent event) {
        System.out.println("[Status #" + event.getId() + "]: The order has been sent! Will be delivered soon!");
    }
}