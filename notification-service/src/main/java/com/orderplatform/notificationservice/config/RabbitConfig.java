package com.orderplatform.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("user.exchange");
    }

    @Bean
    public Queue userRegisteredQueue() {
        return QueueBuilder.durable("notification.user.registered")
                .withArgument("x-dead-letter-exchange", "")
                .withArgument(
                        "x-dead-letter-routing-key",
                        "notification.user.registered.dlq"
                )
                .build();
    }

    // DLQ
    @Bean
    public Queue userRegisteredDlq() {
        return QueueBuilder
                .durable("notification.user.registered.dlq")
                .build();
    }

    @Bean
    public Binding userRegisteredBinding() {
        return BindingBuilder
                .bind(userRegisteredQueue())
                .to(userExchange())
                .with("user.registered");
    }

    @Bean
    public Queue analyticsUserRegisteredQueue() {
        return new Queue("analytics.user.registered");
    }

    @Bean
    public Binding analyticsUserRegisteredBinding(TopicExchange userExchange) {
        return BindingBuilder
                .bind(analyticsUserRegisteredQueue())
                .to(userExchange)
                .with("user.registered");
    }
}
