package com.agentpioneer.producer;

import com.agentpioneer.constants.RabbitMQConstants;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InterviewEvaluationProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendEvaluationTask(Long interviewId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.EXCHANGE_INTERVIEW_EVALUATION,
                RabbitMQConstants.ROUTING_KEY_INTERVIEW_EVALUATION,
                interviewId
        );
    }
}
