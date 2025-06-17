package com.agentpioneer.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResumeVO {
    private Long resumeId;

    /**
     * 原始简历文件名（如"张三_简历.pdf"）
     */
    private String originalFileName;

    /**
     * 异步处理状态
     */
    @Schema(
            description = "异步处理状态 "

    )
    private String status;


    /**
     * 处理失败时的错误信息
     */
    private String errorMessage;

}
