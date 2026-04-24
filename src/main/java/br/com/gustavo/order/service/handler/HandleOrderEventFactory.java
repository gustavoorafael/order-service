package br.com.gustavo.order.service.handler;

import br.com.gustavo.order.service.Topic;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public final class HandleOrderEventFactory {

    private final Map<String, HandleOrderEvent> handlers;

    public HandleOrderEventFactory(
            HandleReceiveOrderEvent receiveHandler,
            HandleApprovedOrderEvent approvedHandler,
            HandleDeliveringOrderEvent deliveringHandler
    ) {
        this.handlers = Map.of(
                Topic.RECEIVED_TOPIC, receiveHandler,
                Topic.APPROVED_TOPIC, approvedHandler,
                Topic.DELIVERING_TOPIC, deliveringHandler
        );
    }

    public HandleOrderEvent getHandler(String topic) {
        HandleOrderEvent handler = handlers.get(topic);
        if (handler == null) throw new IllegalArgumentException("Unknown topic: " + topic);
        return handler;
    }
}