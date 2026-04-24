package br.com.gustavo.order.service.controller;

import br.com.gustavo.order.service.dto.OrderEvent;
import br.com.gustavo.order.service.producer.OrderProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Manages order lifecycle via Kafka")
public class OrderController {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private final OrderProducer producer;

    @Operation(summary = "Place a new order", description = "Publishes to topic-received-orders")
    @PostMapping("/request")
    public ResponseEntity<String> requestOrder(@RequestBody OrderEvent order) {
        producer.sendOrder(order);
        return ResponseEntity.ok("Order sent to Kafka!");
    }

    @Operation(summary = "Ship an order", description = "Publishes to topic-delivering-orders")
    @PostMapping("/ship")
    public ResponseEntity<String> shipOrder(@RequestBody OrderEvent order) {
        producer.shipOrder(order);
        return ResponseEntity.ok("Order has been sent!");
    }
}
