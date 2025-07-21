package com.agentpioneer.config;

import com.agentpioneer.constants.RabbitMQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue interviewEvaluationQueue() {
        return QueueBuilder.durable(RabbitMQConstants.QUEUE_INTERVIEW_EVALUATION).build();
    }

    @Bean
    public TopicExchange interviewEvaluationExchange() {
        return ExchangeBuilder.topicExchange(RabbitMQConstants.EXCHANGE_INTERVIEW_EVALUATION).durable(true).build();
    }

    @Bean
    public Binding interviewEvaluationBinding() {
        return BindingBuilder
                .bind(interviewEvaluationQueue())
                .to(interviewEvaluationExchange())
                .with(RabbitMQConstants.ROUTING_KEY_INTERVIEW_EVALUATION);
    }
};