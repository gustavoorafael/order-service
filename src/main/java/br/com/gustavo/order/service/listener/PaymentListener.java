package br.com.gustavo.order.service.listener;

import br.com.gustavo.order.service.Topic;
import br.com.gustavo.order.service.dto.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class PaymentListener {

    public static final Map<String, BigDecimal> pendingOrders = new ConcurrentHashMap<>();

    @KafkaListener(topics = Topic.RECEIVED_TOPIC, groupId = "payment-group")
    public void processPayment(OrderEvent order) {
        pendingOrders.put(order.getId(), order.getPrice());
        System.out.println("Payment system: " + order.getPrice() + "$ has to paid for the order " + order.getId());
    }

}
