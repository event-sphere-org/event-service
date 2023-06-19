package com.eventsphere.event.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${spring.rabbitmq.template.exchange}")
    private String exchange;

    @Value("${rabbitmq.queue.user.delete}")
    private String userDeleteQueue;

    @Value("${rabbitmq.queue.event.delete}")
    private String eventDeleteQueue;

    @Value("${rabbitmq.queue.category.delete}")
    private String categoryDeleteQueue;

    @Value("${rabbitmq.routing-key.user.delete}")
    private String userDeleteRk;

    @Value("${rabbitmq.routing-key.event.delete}")
    private String eventDeleteRk;

    @Value("${rabbitmq.routing-key.category.delete}")
    private String categoryDeleteRk;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Bean
    Queue userDeleteQueue() {
        return new Queue(userDeleteQueue, true);
    }

    @Bean
    Queue eventDeleteQueue() {
        return new Queue(eventDeleteQueue, true);
    }

    @Bean
    Queue categoryDeleteQueue() {
        return new Queue(categoryDeleteQueue, true);
    }

    @Bean
    Exchange exchange() {
        return ExchangeBuilder.directExchange(exchange).durable(true).build();
    }

    @Bean
    Binding userDeleteBinding() {
        return BindingBuilder
                .bind(userDeleteQueue())
                .to(exchange())
                .with(userDeleteRk)
                .noargs();
    }

    @Bean
    Binding eventDeleteBinding() {
        return BindingBuilder
                .bind(eventDeleteQueue())
                .to(exchange())
                .with(eventDeleteRk)
                .noargs();
    }

    @Bean
    Binding categoryDeleteBinding() {
        return BindingBuilder
                .bind(categoryDeleteQueue())
                .to(exchange())
                .with(categoryDeleteRk)
                .noargs();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);

        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());

        return rabbitTemplate;
    }
}