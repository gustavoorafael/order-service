package br.com.gustavo.order.service;

import br.com.gustavo.order.service.dto.OrderEvent;
import br.com.gustavo.order.service.handler.*;
import br.com.gustavo.order.service.listener.StatusListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusListenerTest {

    @Mock
    private HandleOrderEventFactory factory;

    @Mock
    private HandleOrderEvent handler;

    @InjectMocks
    private StatusListener statusListener;

    @Test
    @DisplayName("Should delegate to correct handler for RECEIVED topic")
    void shouldDelegateToReceivedHandler() {
        OrderEvent event = new OrderEvent("1", "Pizza", new BigDecimal("50.00"));
        when(factory.getHandler(Topic.RECEIVED_TOPIC)).thenReturn(handler);

        statusListener.handleEvent(event, Topic.RECEIVED_TOPIC);

        verify(factory).getHandler(Topic.RECEIVED_TOPIC);
        verify(handler).process(event);
    }

    @Test
    @DisplayName("Should delegate to correct handler for APPROVED topic")
    void shouldDelegateToApprovedHandler() {
        OrderEvent event = new OrderEvent("2", "Burger", new BigDecimal("35.00"));
        when(factory.getHandler(Topic.APPROVED_TOPIC)).thenReturn(handler);

        statusListener.handleEvent(event, Topic.APPROVED_TOPIC);

        verify(factory).getHandler(Topic.APPROVED_TOPIC);
        verify(handler).process(event);
    }

    @Test
    @DisplayName("Should delegate to correct handler for DELIVERING topic")
    void shouldDelegateToDeliveringHandler() {
        OrderEvent event = new OrderEvent("3", "Sushi", new BigDecimal("90.00"));
        when(factory.getHandler(Topic.DELIVERING_TOPIC)).thenReturn(handler);

        statusListener.handleEvent(event, Topic.DELIVERING_TOPIC);

        verify(factory).getHandler(Topic.DELIVERING_TOPIC);
        verify(handler).process(event);
    }

    @Test
    @DisplayName("Should call process exactly once per event")
    void shouldCallProcessExactlyOnce() {
        OrderEvent event = new OrderEvent("4", "Salad", new BigDecimal("20.00"));
        when(factory.getHandler(any())).thenReturn(handler);

        statusListener.handleEvent(event, Topic.RECEIVED_TOPIC);

        verify(handler, times(1)).process(event);
    }
}

class HandleReceiveOrderEventTest {

    @Test
    @DisplayName("Should print waiting payment message without throwing")
    void shouldPrintWaitingPaymentMessage() {
        HandleReceiveOrderEvent handler = new HandleReceiveOrderEvent();
        OrderEvent event = new OrderEvent("1", "Pizza", new BigDecimal("50.00"));

        Assertions.assertThatCode(() -> handler.process(event)).doesNotThrowAnyException();
    }
}

class HandleApprovedOrderEventTest {

    @Test
    @DisplayName("Should print approved message without throwing")
    void shouldPrintApprovedMessage() {
        HandleApprovedOrderEvent handler = new HandleApprovedOrderEvent();
        OrderEvent event = new OrderEvent("2", "Burger", new BigDecimal("35.00"));

        Assertions.assertThatCode(() -> handler.process(event)).doesNotThrowAnyException();
    }
}

class HandleDeliveringOrderEventTest {

    @Test
    @DisplayName("Should print delivering message without throwing")
    void shouldPrintDeliveringMessage() {
        HandleDeliveringOrderEvent handler = new HandleDeliveringOrderEvent();
        OrderEvent event = new OrderEvent("3", "Sushi", new BigDecimal("90.00"));

        Assertions.assertThatCode(() -> handler.process(event)).doesNotThrowAnyException();
    }
}
