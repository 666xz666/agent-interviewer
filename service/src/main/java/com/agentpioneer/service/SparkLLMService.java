package com.agentpioneer.service;

import com.agentpioneer.pojo.bo.ChatBO;
import com.agentpioneer.pojo.xunfei.History;
import com.agentpioneer.result.GraceJSONResult;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.List;

public interface SparkLLMService {
    public Flux<ServerSentEvent<GraceJSONResult>> chatStream(
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    );
}
