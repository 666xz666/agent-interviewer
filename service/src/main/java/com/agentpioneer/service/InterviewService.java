package com.agentpioneer.service;

import com.agentpioneer.pojo.Interview;
import com.agentpioneer.pojo.bo.InterviewBO;
import com.agentpioneer.pojo.bo.InterviewQaBO;
import com.agentpioneer.pojo.bo.InterviewQueryBO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface InterviewService {
    public Interview createInterview(InterviewBO interviewBO, Long userId) throws BusinessException;

    public Flux<ServerSentEvent<GraceJSONResult>> query(
            InterviewQueryBO interviewQueryBO,
            Long userId
    ) throws BusinessException;

    public void saveQA(
            InterviewQaBO interviewQaBO
    ) throws BusinessException;
}
