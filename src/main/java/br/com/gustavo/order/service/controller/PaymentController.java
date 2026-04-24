package br.com.gustavo.order.service.controller;

import br.com.gustavo.order.service.dto.OrderEvent;
import br.com.gustavo.order.service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Confirms payment and publishes to topic-approved-orders")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "Confirm payment for an order")
    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayment(@RequestBody OrderEvent payload) {
        try {
            return ResponseEntity.ok(paymentService.confirmPayment(payload));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }
}