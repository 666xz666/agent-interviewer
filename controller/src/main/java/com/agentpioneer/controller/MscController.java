package com.agentpioneer.controller;

import com.agentpioneer.pojo.bo.CreateVoiceBO;
import com.agentpioneer.pojo.vo.AudioAnalysisVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.xunfei.WebIATWS;
import com.agentpioneer.xunfei.WebTtsWs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "MSC接口", description = "语音相关接口")
@RestController
@RequestMapping("/msc")
public class MscController {
    @Resource
    private WebTtsWs webTtsWs;

    @Value("${xf.config.appId}")
    private String appId;
    @Value("${xf.config.apiKey}")
    private String apiKey;
    @Value("${xf.config.apiSecret}")
    private String apiSecret;

    Logger logger = org.slf4j.LoggerFactory.getLogger(MscController.class);

    @Operation(
            summary = "文字转语音",
            description = "根据提供的文本创建语音，vcn就是ai面试官的apiServiceId。返回MP3文件的链接。"
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

    @Operation(
            summary = "语音转文字",
            description = "语音转文字接口，接收音频文件并返回转换后的文字内容。"
    )
    @PostMapping("/audioAnalysis")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "操作成功",
            content = @Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GraceJSONResult.class),
                    examples = @ExampleObject(
                            name = "成功示例",
                            summary = "语音转文字成功响应示例",
                            value = "{\"status\": 200, \"msg\": \"OK\", \"data\": {\"data\": \"转换后的文字内容\"}}"
                    )
            )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "服务器内部错误",
            content = @Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = GraceJSONResult.class),
                    examples = @ExampleObject(
                            name = "失败示例",
                            summary = "语音转文字失败响应示例",
                            value = "{\"status\": 500, \"msg\": \"服务器内部错误\", \"data\": null}"
                    )
            )
    )
    public GraceJSONResult audioAnalysis(
            @Parameter(
                    name = "file",
                    description = "需要转换的音频文件，支持常见音频格式。",
                    required = true,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary")
                    )
            )
            @RequestParam("file") MultipartFile file
    ) throws Exception {
        WebIATWS webIATWS = new WebIATWS(file, appId, apiSecret, apiKey);
        String resultString = webIATWS.voiceMsgSendToAI(file);
        AudioAnalysisVO audioAnalysisVO = new AudioAnalysisVO();
        audioAnalysisVO.setContent(resultString);
        return GraceJSONResult.ok(audioAnalysisVO);
    }
}