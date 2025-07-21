package com.agentpioneer.service;

import com.agentpioneer.pojo.bo.ChatBO;
import com.agentpioneer.pojo.xunfei.History;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.request.function.SparkFunctionBuilder;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

public interface SparkLLMService {
    public Flux<ServerSentEvent<GraceJSONResult>> chatStream(
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    );

    public String chat(
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    ) throws BusinessException;

    public Map<String, Object> functionCall(
            SparkFunctionBuilder sparkFunctionBuilder,
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    ) throws BusinessException;
}
