package br.com.gustavo.order.service.handler;

import br.com.gustavo.order.service.dto.OrderEvent;

public interface HandleOrderEvent {

    void process(OrderEvent event);
}
