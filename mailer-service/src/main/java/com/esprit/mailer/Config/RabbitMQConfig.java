package com.esprit.mailer.Config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue mailQueue() {
        return new Queue("mailer-queue", true);
    }

    @Bean
    public TopicExchange mailExchange() {
        return new TopicExchange("mailer-exchange");
    }

    @Bean
    public Binding mailBinding(Queue mailQueue, TopicExchange mailExchange) {
        return BindingBuilder.bind(mailQueue).to(mailExchange).with("mailer-routing-key");
    }
}
