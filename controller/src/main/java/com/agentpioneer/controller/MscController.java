package com.agentpioneer.controller;

import com.agentpioneer.pojo.bo.CreateVoiceBO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.xunfei.WebTtsWs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MSC接口", description = "语音相关接口")
@RestController
@RequestMapping("/msc")
public class MscController {
    @Resource
    private WebTtsWs webTtsWs;

    Logger logger = org.slf4j.LoggerFactory.getLogger(MscController.class);

    @Operation(
            summary = "创建语音",
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
}
