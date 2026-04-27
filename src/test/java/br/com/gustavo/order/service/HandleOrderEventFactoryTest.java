package br.com.gustavo.order.service;

import br.com.gustavo.order.service.dto.OrderEvent;
import br.com.gustavo.order.service.handler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class HandleOrderEventFactoryTest {

    private HandleOrderEventFactory factory;

    @BeforeEach
    void setUp() {
        factory = new HandleOrderEventFactory(
                new HandleReceiveOrderEvent(),
                new HandleApprovedOrderEvent(),
                new HandleDeliveringOrderEvent()
        );
    }

    @Test
    @DisplayName("Should return HandleReceiveOrderEvent for RECEIVED topic")
    void shouldReturnReceiveHandlerForReceivedTopic() {
        HandleOrderEvent handler = factory.getHandler(Topic.RECEIVED_TOPIC);

        assertThat(handler).isInstanceOf(HandleReceiveOrderEvent.class);
    }

    @Test
    @DisplayName("Should return HandleApprovedOrderEvent for APPROVED topic")
    void shouldReturnApprovedHandlerForApprovedTopic() {
        HandleOrderEvent handler = factory.getHandler(Topic.APPROVED_TOPIC);

        assertThat(handler).isInstanceOf(HandleApprovedOrderEvent.class);
    }

    @Test
    @DisplayName("Should return HandleDeliveringOrderEvent for DELIVERING topic")
    void shouldReturnDeliveringHandlerForDeliveringTopic() {
        HandleOrderEvent handler = factory.getHandler(Topic.DELIVERING_TOPIC);

        assertThat(handler).isInstanceOf(HandleDeliveringOrderEvent.class);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for unknown topic")
    void shouldThrowExceptionForUnknownTopic() {
        assertThatThrownBy(() -> factory.getHandler("topic-unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown topic: topic-unknown");
    }

    @Test
    @DisplayName("Should execute process without throwing for RECEIVED event")
    void shouldProcessReceivedEventWithoutThrowing() {
        OrderEvent event = new OrderEvent("1", "Pizza", new BigDecimal("50.00"));
        HandleOrderEvent handler = factory.getHandler(Topic.RECEIVED_TOPIC);

        assertThatCode(() -> handler.process(event)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should execute process without throwing for APPROVED event")
    void shouldProcessApprovedEventWithoutThrowing() {
        OrderEvent event = new OrderEvent("2", "Burger", new BigDecimal("35.00"));
        HandleOrderEvent handler = factory.getHandler(Topic.APPROVED_TOPIC);

        assertThatCode(() -> handler.process(event)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should execute process without throwing for DELIVERING event")
    void shouldProcessDeliveringEventWithoutThrowing() {
        OrderEvent event = new OrderEvent("3", "Sushi", new BigDecimal("90.00"));
        HandleOrderEvent handler = factory.getHandler(Topic.DELIVERING_TOPIC);

        assertThatCode(() -> handler.process(event)).doesNotThrowAnyException();
    }
}
