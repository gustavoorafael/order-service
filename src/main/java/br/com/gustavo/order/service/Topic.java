package br.com.gustavo.order.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Topic {
    RECEIVED("topic-received-orders"),
    APPROVED("topic-approved-orders"),
    DELIVERING("topic-delivering-orders");

    public static final String RECEIVED_TOPIC = "topic-received-orders";
    public static final String APPROVED_TOPIC = "topic-approved-orders";
    public static final String DELIVERING_TOPIC = "topic-delivering-orders";

    private final String topicName;
}