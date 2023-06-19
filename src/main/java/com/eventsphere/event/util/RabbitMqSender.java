package com.eventsphere.event.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqSender {

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.event.delete}")
    private String eventDeleteRk;

    @Value("${rabbitmq.routing-key.category.delete}")
    private String categoryDeleteRk;

    public void sendDeletedEventId(Long id) {
        rabbitTemplate.convertAndSend(exchange, eventDeleteRk, id);
    }

    public void sendDeletedCategoryId(Long id) {
        rabbitTemplate.convertAndSend(exchange, categoryDeleteRk, id);
    }
}
