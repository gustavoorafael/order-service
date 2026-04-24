package br.com.gustavo.order.service.service;

import br.com.gustavo.order.service.Topic;
import br.com.gustavo.order.service.listener.PaymentListener;
import br.com.gustavo.order.service.dto.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PaymentService {

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public String confirmPayment(OrderEvent payload) {
        String orderId = payload.getId();
        String orderName = payload.getProduct();
        BigDecimal paidValue = payload.getPrice();

        BigDecimal expectedValue = PaymentListener.pendingOrders.get(orderId);

        if (expectedValue == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found on payment system.");
        }

        if (paidValue.compareTo(expectedValue) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong value! Expected value: " + expectedValue);
        }

        OrderEvent approvedOrder = new OrderEvent(orderId, orderName, paidValue);
        kafkaTemplate.send(Topic.APPROVED.getTopicName(), orderId, approvedOrder);

        PaymentListener.pendingOrders.remove(orderId);

        return "Payment approved for order: " + orderId;
    }
}