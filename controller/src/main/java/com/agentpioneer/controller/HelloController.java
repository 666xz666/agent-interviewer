package com.agentpioneer.controller;


import com.agentpioneer.pojo.bo.ChatBO;
import com.agentpioneer.pojo.bo.CreateVoiceBO;
import com.agentpioneer.pojo.bo.ResumeBO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.MilvusService;
import com.agentpioneer.service.SparkLLMService;
import com.agentpioneer.xunfei.WebTtsWs;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.langchain4j.community.model.qianfan.QianfanChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import io.github.briqt.spark4j.SparkClient;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.exception.SparkException;
import io.github.briqt.spark4j.listener.SparkBaseListener;
import io.github.briqt.spark4j.model.SparkMessage;
import io.github.briqt.spark4j.model.SparkRequestBuilder;
import io.github.briqt.spark4j.model.SparkSyncChatResponse;
import io.github.briqt.spark4j.model.request.SparkRequest;
import io.github.briqt.spark4j.model.request.function.SparkFunctionBuilder;
import io.github.briqt.spark4j.model.response.SparkResponse;
import io.github.briqt.spark4j.model.response.SparkResponseFunctionCall;
import io.github.briqt.spark4j.model.response.SparkResponseUsage;
import io.github.briqt.spark4j.model.response.SparkTextUsage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(
        name = "测试接口",
        description = "用于测试的接口"
)
@RestController
@RequestMapping("/test")
public class HelloController {

    Logger logger = org.slf4j.LoggerFactory.getLogger(HelloController.class);

    @Autowired
    private SparkClient sparkClient;

    @Resource
    private WebTtsWs webTtsWs;

    @Autowired
    SparkLLMService sparkLLMService;

    @Operation(
            summary = "创建语音",
            description = "根据提供的文本创建语音。",
            tags = {"测试接口"}
    )
    @PostMapping("/createVoice")
    public GraceJSONResult createVoice(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "创建语音请求体",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "创建语音示例",
                                            summary = "创建语音请求体示例",
                                            value = "{\"content\": \"你好，我叫许战，来自中国矿业大学\", \"vcn\": \"aisjiuxu\"}"
                                    )
                            }
                    )
            )
            CreateVoiceBO createVoiceBO
    ) {
        String resultString = null;
        try {
            resultString = webTtsWs.createVoice(
                    createVoiceBO.getContent(), createVoiceBO.getVcn()
            );
            Thread.sleep(3000);//等语音生成完成了再关闭，给一点等待时间
        } catch (BusinessException e) {
            logger.error(e.getStatus().msg());
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return GraceJSONResult.errorCustom(ResponseStatusEnum.FILE_UPLOAD_FAILD);
        }

        return GraceJSONResult.ok(resultString);
    }

    @Value("${xf.config.appId}")
    private String appId;

    /**
     * @ Description: 测试路由
     * @ return
     */
    @Operation(
        summary = "测试路由",
        description = "这是一个用于测试的路由接口，返回欢迎信息。",
        tags = {"测试接口"}
    )
    @Parameter(
        name = "name",
        in = ParameterIn.QUERY,
        description = "可选的用户名参数",
        schema = @Schema(type = "string")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取欢迎信息", content = @Content(schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "请求参数错误", content = @Content),
        @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content)
    })
    @GetMapping("hello")
    public Object hello(@RequestParam(required = false) String name) {
        System.out.println(appId);
        if (name != null) {
            return "Hello " + name + "! Welcome to pioneer.";
        }
        return "hello pioneer";
    }

//    @PostMapping("resumeTest2")
//    @Operation(summary = "Spark 函数调用测试", description = "测试 Spark 函数调用接口")
//    public GraceJSONResult resumeTest2(
//            @RequestBody
//            ResumeBO resumeBO
//    ) {
//
//        return GraceJSONResult.ok();
//    }

//    @GetMapping("spark-chat")
//    @Operation(summary = "Spark 对话测试", description = "测试 Spark 对话接口")
//    public String spark() {
//        // 消息列表，可以在此列表添加历史对话记录
//        List<SparkMessage> messages = new ArrayList<>();
//        messages.add(SparkMessage.systemContent("请你扮演我的语文老师李老师，问我讲解问题问题，希望你可以保证知识准确，逻辑严谨。"));
//        messages.add(SparkMessage.userContent("鲁迅和周树人小时候打过架吗？"));
//        // 构造请求
//        SparkRequest sparkRequest = SparkRequest.builder()
//                // 消息列表
//                .messages(messages)
//                // 模型回答的tokens的最大长度,非必传，默认为2048。
//                // V1.5取值为[1,4096]
//                // V2.0取值为[1,8192]
//                // V3.0取值为[1,8192]
//                .maxTokens(2048)
//                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
//                .temperature(0.2)
//                // 指定请求版本，默认使用最新3.0版本
//                .apiVersion(SparkApiVersion.V3_0)
//                .build();
//
//        try {
//            // 同步调用
//            SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
//            SparkTextUsage textUsage = chatResponse.getTextUsage();
//
//            System.out.println("\n回答：" + chatResponse.getContent());
//            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
//                    + "，回答tokens：" + textUsage.getCompletionTokens()
//                    + "，总消耗tokens：" + textUsage.getTotalTokens());
//            return chatResponse.getContent();
//        } catch (SparkException e) {
//            System.out.println("发生异常了：" + e.getMessage());
//            return e.getMessage();
//        }
//    }

//    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<GraceJSONResult>> chatStream(@RequestBody ChatBO chatBO) {
//        return sparkLLMService.chatStream(
//                "你是一个对话助手",
//                chatBO,
//                2048,
//                0.2,
//                SparkApiVersion.V3_0
//        );
//    }

    @GetMapping("spark-functioncall")
    @Operation(summary = "Spark 函数调用测试", description = "测试 Spark 函数调用接口")
    public String sparkFunctioncall() {
        // 消息列表，可以在此列表添加历史对话记录
        List<SparkMessage> messages = new ArrayList<>();
        messages.add(SparkMessage.userContent("科大讯飞的最新股票价格是多少"));

        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 使用functionCall功能版本需要大于等于3.0
                .apiVersion(SparkApiVersion.V3_0)
                // 添加方法，可多次调用添加多个方法
                .addFunction(
                        // 回调时回传的方法名
                        SparkFunctionBuilder.functionName("stockPrice")
                                // 让大模型理解方法意图 方法描述
                                .description("根据公司名称查询最新股票价格")
                                // 方法需要的参数。可多次调用添加多个参数
                                .addParameterProperty("companyName", "string", "公司名称")
                                // 指定以上的参数哪些是必传的
                                .addParameterRequired("companyName").build()
                ).build();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println("request：" + objectMapper.writeValueAsString(sparkRequest));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 同步调用
        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
        SparkTextUsage textUsage = chatResponse.getTextUsage();
        SparkResponseFunctionCall functionCall = chatResponse.getFunctionCall();

        try {
            if (functionCall != null) {
                String functionName = functionCall.getName();
                Map<String, Object> arguments = functionCall.getMapArguments();

                System.out.println("\n收到functionCall：方法名称：" + functionName + "，参数：" + objectMapper.writeValueAsString(arguments));

                // 在这里根据方法名和参数自行调用方法实现
                return "收到 functionCall：方法名称：" + functionName + "，参数：" + objectMapper.writeValueAsString(arguments);
            } else {
                System.out.println("\n回答：" + chatResponse.getContent());
                return chatResponse.getContent();
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                    + "，回答tokens：" + textUsage.getCompletionTokens()
                    + "，总消耗tokens：" + textUsage.getTotalTokens());
        }
    }

//    @GetMapping("spark-functioncall-score")
//    @Operation(summary = "Spark 函数调用测试评分", description = "function call 评分测试接口")
//    public String sparkFunctioncallScore() {
//        // 编写面试记录
//        String interviewRecord = "候选人在面试中，专业知识回答基本正确，但对于一些深入的问题回答不够准确。沟通表达较为流畅，但缺乏自信，声音较小。团队协作方面，能举例说明自己的经验，但案例不够具体。";
//
//        // 消息列表
//        List<SparkMessage> messages = new ArrayList<>();
//        messages.add(SparkMessage.userContent("请根据以下面试记录从专业知识、沟通表达、团队协作三个方面对候选人进行评分，分数限定在 0 - 100 之间：" + interviewRecord));
//
//        // 构造请求
//        SparkRequest sparkRequest = new SparkRequestBuilder()
//                .messages(messages)
//                .apiVersion(SparkApiVersion.V3_0)
//                .addFunction(
//                        SparkFunctionBuilder.functionName("interviewScoring")
//                                .description("根据面试记录从专业知识、沟通表达、团队协作三个方面对候选人进行评分，分数限定在 0 - 100 之间")
//                                .addParameterProperty("interviewRecord", "string", "面试记录")
//                                .addParameterRequired("interviewRecord").build()
//                ).build();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            System.out.println("request：" + objectMapper.writeValueAsString(sparkRequest));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 同步调用
//        SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
//        SparkTextUsage textUsage = chatResponse.getTextUsage();
//        SparkResponseFunctionCall functionCall = chatResponse.getFunctionCall();
//
//        if (functionCall != null) {
//            String functionName = functionCall.getName();
//            Map<String, Object> arguments = functionCall.getMapArguments();
//
//            try {
//                System.out.println("\n收到 functionCall：方法名称：" + functionName + "，参数：" + objectMapper.writeValueAsString(arguments));
//
//                if ("interviewScoring".equals(functionName)) {
//                    String record = (String) arguments.get("interviewRecord");
//                    Map<String, Integer> scores = performInterviewScoring(record);
//                    return "专业知识评分：" + scores.get("professionalKnowledge") + " 分，沟通表达评分：" + scores.get("communication") + " 分，团队协作评分：" + scores.get("teamwork") + " 分";
//                }
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return "未触发 functionCall，回答：" + chatResponse.getContent();
//    }

    /**
     * 根据面试记录进行面试评分
     *
     * @param interviewRecord 面试记录
     * @return 包含三个方面评分的 Map
     */
    private static Map<String, Integer> performInterviewScoring(String interviewRecord) {
        Map<String, Integer> scores = new HashMap<>();

        // 专业知识评分
        if (interviewRecord.contains("基本正确") && interviewRecord.contains("不够准确")) {
            scores.put("professionalKnowledge", 70);
        } else {
            scores.put("professionalKnowledge", 50);
        }

        // 沟通表达评分
        if (interviewRecord.contains("较为流畅") && interviewRecord.contains("缺乏自信，声音较小")) {
            scores.put("communication", 60);
        } else {
            scores.put("communication", 50);
        }

        // 团队协作评分
        if (interviewRecord.contains("能举例说明") && interviewRecord.contains("案例不够具体")) {
            scores.put("teamwork", 65);
        } else {
            scores.put("teamwork", 50);
        }

        return scores;
    }

    @Resource
    private MilvusService milvusService;

    /**
     * 测试创建集合
     *
     * @return 操作结果信息
     */
    @GetMapping("/createCollection")
    public String createCollection() {
        String collectionName = "test_collection";
        String desc = "This is a test collection";
        milvusService.create(collectionName, desc);
        return "Collection " + collectionName + " created successfully";
    }

    /**
     * 测试插入数据
     *
     * @return 操作结果信息
     */
    @GetMapping("/insertData")
    public String insertData() {
        String collectionName = "test_collection";
        List<Long> fileIds = new ArrayList<>();
        fileIds.add(1L);
        fileIds.add(2L);

        List<Long> textIds = new ArrayList<>();
        textIds.add(1L);
        textIds.add(2L);

        List<List<Float>> vectorList = new ArrayList<>();
        List<Float> vector1 = new ArrayList<>();
        vector1.add(0.1f);
        vector1.add(0.2f);
        vectorList.add(vector1);

        List<Float> vector2 = new ArrayList<>();
        vector2.add(0.3f);
        vector2.add(0.4f);
        vectorList.add(vector2);

        boolean result = milvusService.insert(collectionName, textIds, vectorList, fileIds);
        return result ? "Data inserted successfully" : "Data insertion failed";
    }

    /**
     * 测试向量搜索
     *
     * @return 搜索结果
     */
    @GetMapping("/searchData")
    public List<Long> searchData() {
        String collectionName = "test_collection";
        int topK = 2;
        List<List<Float>> vectorList = new ArrayList<>();
        List<Float> searchVector = new ArrayList<>();
        searchVector.add(0.15f);
        searchVector.add(0.25f);
        vectorList.add(searchVector);

        return milvusService.search(collectionName, topK, vectorList);
    }

    /**
     * 测试删除集合
     *
     * @return 操作结果信息
     */
    @GetMapping("/dropCollection")
    public String dropCollection() {
        String collectionName = "test_collection";
        milvusService.dropCollect(collectionName);
        return "Collection " + collectionName + " dropped successfully";
    }


    @GetMapping("qwen")
    public GraceJSONResult qwen() {
        String promptTemplate = "请根据以下面试者的表现，按照给定的评价指标进行评分（0-100分）并给出评价字符串。\n";

//        QianfanChatModel model =
//                QianfanChatModel.builder()
//                        .apiKey("FNNwfaQBIWOtZvR0wPJmENZs")
//                        .secretKey("hTMVLkcraQAiP3A4aZE1H0BVn2Iu1ENt")
//                        .modelName("Yi-34B-Chat")
//                        .build();

//        OpenAiChatModel model = OpenAiChatModel.builder()
//                .apiKey("bce-v3/ALTAK-MszP7E8rrMmfHV15sRFUz/a255f2e99ac1c54dda182c5e5a1e5bc43d9e5395")
//                .baseUrl("https://qianfan.baidubce.com/v2")
//                .modelName("ernie-3.5-8k")
//                .strictJsonSchema(true)
//                .build();
        QianfanChatModel model = QianfanChatModel.builder()
                .apiKey("FNNwfaQBIWOtZvR0wPJmENZs")
                .secretKey("hTMVLkcraQAiP3A4aZE1H0BVn2Iu1ENt")
                .modelName("Yi-34B-Chat")
                .build();

//        class
//
//        interface SentimentAnalyzer {
//            @UserMessage("Does {{it}} has a positive sentiment?")
//            boolean isPositive(String text);
//        }
//
//        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);
//
//        boolean positive = sentimentAnalyzer.isPositive("It's wonderful!");
//
//        return GraceJSONResult.ok(positive);

        // you can add an optional description to help an LLM have a better understanding
        class Address {
            String street;
            Integer streetNumber;
            String city;
        }

        class Person {

            @Description("first name of a person") // you can add an optional description to help an LLM have a better understanding
            String firstName;
            String lastName;
            LocalDate birthDate;
            @Description("an address")
            Address address;
        }


        interface PersonExtractor {

            @UserMessage("Extract information about a person from {{it}}")
            Person extractPersonFrom(String text);
        }

        PersonExtractor personExtractor = AiServices.create(PersonExtractor.class, model);

        String text = """
                In 1968, amidst the fading echoes of Independence Day,
                a child named John arrived under the calm evening sky.
                This newborn, bearing the surname Doe, marked the start of a new journey.
                He was welcomed into the world at 345 Whispering Pines Avenue
                a quaint street nestled in the heart of Springfield
                an abode that echoed with the gentle hum of suburban dreams and aspirations.
                """;

        Person person = personExtractor.extractPersonFrom(text);

        System.out.println(person);

        return GraceJSONResult.ok(person);
    }
}