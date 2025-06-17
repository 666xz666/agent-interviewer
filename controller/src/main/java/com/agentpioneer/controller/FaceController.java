package com.agentpioneer.controller;

import com.agentpioneer.utils.OssUtils;
import com.agentpioneer.xunfei.Face;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(
        name = "人脸识别",
        description = "人脸识别"
)
@RestController
@RequestMapping("/face")
public class FaceController {
    @Resource
    private OssUtils ossUtils;

    @Resource
    private Face face;

    @Operation(
            summary = "人脸识别",
            description = "rate ：介于0-1间的浮点数，表示该图像被识别为某个分类的概率值，概率越高、机器越肯定\n" +
                    "                rates：各个label分别对应的概率值的数组，顺序如label的大小从小到大的顺序\n" +
                    "                label：大于等于0时，表明图片属于哪个分类或结果；等于-1时，代表该图片文件有错误，或者格式不支持（gif图不支持）\n" +
                    "                name：图片的url地址或名称\n" +
                    "                review：本次识别结果是否存在偏差，返回true时存在偏差，可信度较低，返回false时可信度较高，具体可参考rate参数值\n" +
                    "                \n" +
                    "                label的值及其对应的表情\n" +
                    "                0：其他(非人脸表情图片)\n" +
                    "                1：其他表情\n" +
                    "                2：喜悦\n" +
                    "                3：愤怒\n" +
                    "                4：悲伤\n" +
                    "                5：惊恐\n" +
                    "                6：厌恶\n" +
                    "                7：中性\n"

    )
    @PostMapping("/expressionAnalysis")
    public String expressionAnalysis(
            @Parameter(
                    name = "file",
                    description = "人脸图片文件",
                    required = true,
                    in = ParameterIn.DEFAULT,
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary"),
                            encoding = @Encoding(name = "file")
                    )
            )
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        String fileName = file.getOriginalFilename();
        return face.getFaceExpression(fileName, file);
    }

}
