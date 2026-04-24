package br.com.gustavo.order.service.listener;

import br.com.gustavo.order.service.handler.HandleOrderEvent;
import br.com.gustavo.order.service.handler.HandleOrderEventFactory;
import br.com.gustavo.order.service.Topic;
import br.com.gustavo.order.service.dto.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class StatusListener {
    private final HandleOrderEventFactory factory;

    public StatusListener(HandleOrderEventFactory factory) {
        this.factory = factory;
    }

    @KafkaListener(
            topics = {Topic.RECEIVED_TOPIC, Topic.APPROVED_TOPIC, Topic.DELIVERING_TOPIC},
            groupId = "status-group"
    )
    public void handleEvent(OrderEvent order, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        factory.getHandler(topic).process(order);
    }
}