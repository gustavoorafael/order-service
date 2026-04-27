package br.com.gustavo.order.service;

import br.com.gustavo.order.service.listener.PaymentListener;
import br.com.gustavo.order.service.dto.OrderEvent;
import br.com.gustavo.order.service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        PaymentListener.pendingOrders.clear();
    }

    @Test
    @DisplayName("Should approve payment when paid value matches expected")
    void shouldApprovePaymentWhenValueMatches() {
        PaymentListener.pendingOrders.put("order-1", new BigDecimal("50.00"));
        OrderEvent payload = new OrderEvent("order-1", "Pizza", new BigDecimal("50.00"));

        String result = paymentService.confirmPayment(payload);

        assertThat(result).isEqualTo("Payment approved for order: order-1");
        assertThat(PaymentListener.pendingOrders).doesNotContainKey("order-1");
        verify(kafkaTemplate).send(eq(Topic.APPROVED.getTopicName()), eq("order-1"), any(OrderEvent.class));
    }

    @Test
    @DisplayName("Should approve payment when paid value is greater than expected")
    void shouldApprovePaymentWhenValueIsGreater() {
        PaymentListener.pendingOrders.put("order-2", new BigDecimal("50.00"));
        OrderEvent payload = new OrderEvent("order-2", "Pizza", new BigDecimal("60.00"));

        String result = paymentService.confirmPayment(payload);

        assertThat(result).isEqualTo("Payment approved for order: order-2");
        verify(kafkaTemplate).send(eq(Topic.APPROVED.getTopicName()), eq("order-2"), any(OrderEvent.class));
    }

    @Test
    @DisplayName("Should throw NOT_FOUND when order does not exist in pending orders")
    void shouldThrowNotFoundWhenOrderDoesNotExist() {
        OrderEvent payload = new OrderEvent("order-999", "Pizza", new BigDecimal("50.00"));

        assertThatThrownBy(() -> paymentService.confirmPayment(payload))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(rse.getReason()).isEqualTo("Order not found on payment system.");
                });

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    @DisplayName("Should throw BAD_REQUEST when paid value is less than expected")
    void shouldThrowBadRequestWhenValueIsInsufficient() {
        PaymentListener.pendingOrders.put("order-3", new BigDecimal("100.00"));
        OrderEvent payload = new OrderEvent("order-3", "Pizza", new BigDecimal("40.00"));

        assertThatThrownBy(() -> paymentService.confirmPayment(payload))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(rse.getReason()).contains("Wrong value! Expected value: 100.00");
                });

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    @DisplayName("Should remove order from pending orders after approval")
    void shouldRemoveOrderFromPendingAfterApproval() {
        PaymentListener.pendingOrders.put("order-4", new BigDecimal("75.00"));
        OrderEvent payload = new OrderEvent("order-4", "Burger", new BigDecimal("75.00"));

        paymentService.confirmPayment(payload);

        assertThat(PaymentListener.pendingOrders).isEmpty();
    }
}
