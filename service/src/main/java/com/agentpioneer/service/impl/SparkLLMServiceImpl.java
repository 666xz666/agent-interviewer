package com.agentpioneer.service.impl;

import com.agentpioneer.pojo.bo.ChatBO;
import com.agentpioneer.pojo.enums.ChatRoleEnum;
import com.agentpioneer.pojo.vo.ChatVO;
import com.agentpioneer.pojo.xunfei.History;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.service.SparkLLMService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.listener.SparkBaseListener;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.request.function.SparkFunctionBuilder;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseFunctionCall;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import okhttp3.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SparkLLMServiceImpl implements SparkLLMService {

    @Autowired
    private SparkClient sparkClient;

    @Override
    public Flux<ServerSentEvent<GraceJSONResult>> chatStream(
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    ) {
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(systemPrompt));

        // 处理历史记录
        List<History> histories = chatBO.getHistories();
        if (histories != null) {
            histories.stream()
                    .filter(h -> h != null && h.getRole() != null && h.getContent() != null) // 可选：再加一层防御
                    .map(h -> {
                        if (ChatRoleEnum.USER.equals(h.getRole())) {
                            return SparkMessage.userContent(h.getContent());
                        } else {
                            return SparkMessage.assistantContent(h.getContent());
                        }
                    })
                    .forEach(messages::add);
        }

        messages.add(SparkMessage.userContent(chatBO.getContent()));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messages)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .apiVersion(version)
                .build();

        Sinks.Many<ServerSentEvent<GraceJSONResult>> sink = Sinks.many()
                .unicast()
                .onBackpressureBuffer();

        // 自定义监听器
        SparkBaseListener listener = new SparkBaseListener() {
            @Override
            public void onMessage(String content, SparkResponseUsage usage, Integer status, SparkRequest sparkRequest, SparkResponse sparkResponse, WebSocket webSocket) {
                ChatVO chatVO = new ChatVO();
                chatVO.setContent(content);
                if (status == 2) {
                    chatVO.setIsEnd(true);
                } else {
                    chatVO.setIsEnd(false);
                }
                GraceJSONResult result = GraceJSONResult.ok(chatVO);
                sink.tryEmitNext(ServerSentEvent.builder(result).build());
                if (status == 2) {
                    sink.tryEmitComplete();
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                System.out.println(t.getMessage());
                GraceJSONResult result = GraceJSONResult.error();
                sink.tryEmitNext(ServerSentEvent.builder(result).build());
            }
        };

        // 流式调用
        sparkClient.chatStream(sparkRequest, listener);

        return sink.asFlux();
    }

    @Override
    public String chat(
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    ) throws BusinessException {
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(systemPrompt));

        // 处理历史记录
        List<History> histories = chatBO.getHistories();
        if (histories != null) {
            histories.stream()
                    .filter(h -> h != null && h.getRole() != null && h.getContent() != null) // 可选：再加一层防御
                    .map(h -> {
                        if (ChatRoleEnum.USER.equals(h.getRole())) {
                            return SparkMessage.userContent(h.getContent());
                        } else {
                            return SparkMessage.assistantContent(h.getContent());
                        }
                    })
                    .forEach(messages::add);
        }

        messages.add(SparkMessage.userContent(chatBO.getContent()));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messages)
                .apiVersion(version)
                .build();

        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        return chatResponse.getContent();
    }

    @Override
    public Map<String, Object> functionCall(
            SparkFunctionBuilder sparkFunctionBuilder,
            String systemPrompt,
            ChatBO chatBO,
            Integer maxTokens,
            Double temperature,
            SparkApiVersion version
    ) throws BusinessException {
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.systemContent(systemPrompt));

        // 处理历史记录
        List<History> histories = chatBO.getHistories();
        if (histories != null) {
            histories.stream()
                    .filter(h -> h != null && h.getRole() != null && h.getContent() != null) // 可选：再加一层防御
                    .map(h -> {
                        if (ChatRoleEnum.USER.equals(h.getRole())) {
                            return SparkMessage.userContent(h.getContent());
                        } else {
                            return SparkMessage.assistantContent(h.getContent());
                        }
                    })
                    .forEach(messages::add);
        }

        messages.add(SparkMessage.userContent(chatBO.getContent()));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                .messages(messages)
                .apiVersion(version)
                .addFunction(sparkFunctionBuilder.build())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println("request：" + objectMapper.writeValueAsString(sparkRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        System.out.println(chatResponse.getContent());
        SparkResponseFunctionCall functionCall = chatResponse.getFunctionCall();
        return functionCall.getMapArguments();
    }
}
