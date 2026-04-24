package br.com.gustavo.order.service.handler;

import br.com.gustavo.order.service.dto.OrderEvent;
import org.springframework.stereotype.Component;

@Component
public class HandleApprovedOrderEvent implements HandleOrderEvent {

    @Override
    public void process(OrderEvent event) {
        System.out.println("[Status #" + event.getId() + "]: Order successfully paid! Preparing order!");
    }
}