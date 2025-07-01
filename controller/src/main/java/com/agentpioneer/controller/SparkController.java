package com.agentpioneer.controller;

import com.agentpioneer.pojo.bo.ChatBO;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.service.SparkLLMService;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(
        name = "星火大模型",
        description = "星火大模型相关接口"
)
@RestController("spark")
public class SparkController {
    @Autowired
    private SparkLLMService sparkLLMService;


    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(
            summary = "星火大模型流式对话",
            description = "星火大模型流式对话"
    )
    public Flux<ServerSentEvent<GraceJSONResult>> chatStream(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "对话信息",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "对话信息示例",
                                            summary = "对话信息请求体示例",
                                            value = "{\n" +
                                                    "  \"content\": \"请总结我们的对话内容\",\n" +
                                                    "  \"histories\": [\n" +
                                                    "  {\n" +
                                                    "\t\"role\":\"user\",\n" +
                                                    "\t\"content\": \"1+1等于几\"\n" +
                                                    "  },\n" +
                                                    "  {\n" +
                                                    "\t\"role\":\"assistant\",\n" +
                                                    "\t\"content\": \"等于2\"\n" +
                                                    "  }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            }
                    )
            )
            ChatBO chatBO
    ) {
        return sparkLLMService.chatStream(
                "你是一个专业的中文助手",
                chatBO,
                8000,
                0.2,
                SparkApiVersion.V3_0
        );
    }
}
