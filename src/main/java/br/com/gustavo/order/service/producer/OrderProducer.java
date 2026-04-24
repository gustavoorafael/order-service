package br.com.gustavo.order.service.producer;

import br.com.gustavo.order.service.Topic;
import br.com.gustavo.order.service.dto.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderProducer {
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public void sendOrder(OrderEvent order) {
        kafkaTemplate.send(Topic.RECEIVED.getTopicName(), order.getId(), order)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println("Failed to send order: " + order.getId() + " | Error: " + ex.getMessage());
                    } else {
                        System.out.println("Order successfully received: " + order.getId());
                    }
                });
    }

    public void shipOrder(OrderEvent order) {
        kafkaTemplate.send(Topic.DELIVERING.getTopicName(), order.getId(), order)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.out.println("Failed to ship order: " + order.getId() + " | Error: " + ex.getMessage());
                    } else {
                        System.out.println("Order successfully shipped: " + order.getId());
                    }
                });
    }


}
