package com.agentpioneer.consumer;

import com.agentpioneer.constants.RabbitMQConstants;
import com.agentpioneer.service.InterviewService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InterviewEvaluationConsumer {
    @Autowired
    private InterviewService interviewService;

    @RabbitListener(queues = RabbitMQConstants.QUEUE_INTERVIEW_EVALUATION)
    public void handleInterviewEvaluation(Long interviewId) {
        // 执行业务逻辑：对 interviewId 进行面试评估
        System.out.println("开始处理面试评估任务，interviewId: " + interviewId.toString());
        interviewService.evalInterview(interviewId);
    }
}