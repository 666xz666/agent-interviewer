package com.agentpioneer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
//    @Bean
//    public Jackson2JsonMessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
    public static final String QUEUE_NAME_1 = "agentpioneer_queue_1";
    public static final String QUEUE_NAME_2 = "agentpioneer_queue_2";
    public static final String EXCHANGE_NAME_1 = "agentpioneer_exchange_1";
    public static final String EXCHANGE_NAME_2 = "agentpioneer_exchange_2";
    public static final String ROUTING_KEY_1 = "agentpioneer.routingkey_1";
    public static final String ROUTING_KEY_2 = "agentpioneer.routingkey_2";

    @Bean
    public Queue queue1() {
        return new Queue(QUEUE_NAME_1, true);
    }

    @Bean
    public Queue queue2() {
        return new Queue(QUEUE_NAME_2, true);
    }

    @Bean
    public TopicExchange exchange1() {
        return new TopicExchange(EXCHANGE_NAME_1);
    }

    @Bean
    public TopicExchange exchange2() {
        return new TopicExchange(EXCHANGE_NAME_2);
    }

    @Bean
    public Binding binding1(Queue queue1, TopicExchange exchange1) {
        return BindingBuilder.bind(queue1).to(exchange1).with(ROUTING_KEY_1);
    }

    @Bean
    public Binding binding2(Queue queue2, TopicExchange exchange2) {
        return BindingBuilder.bind(queue2).to(exchange2).with(ROUTING_KEY_2);
    }
}