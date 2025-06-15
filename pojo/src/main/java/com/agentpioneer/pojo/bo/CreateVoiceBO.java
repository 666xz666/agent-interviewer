package com.agentpioneer.pojo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "创建声音请求体")
public class CreateVoiceBO {
    @Schema(description = "要转语音的文本内容", example = "你好，我叫许战，来自中国矿业大学")
    private String content;
    @Schema(description = "发音人id", example = "aisjiuxu")
    private String vcn;
}
