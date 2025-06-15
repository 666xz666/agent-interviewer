package com.agentpioneer.controller;

import com.agentpioneer.utils.OssUtils;
import com.agentpioneer.xunfei.Face;
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
        String faceExpression = face.getFaceExpression(fileName, file);
        return faceExpression;
    }

}
