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
public class ResumeContentVO {
    private Long resumeId;

    /**
     * 原始简历文件名（如"张三_简历.pdf"）
     */
    private String originalFileName;

    /**
     * 结构化简历数据（个人信息、教育背景等）
     */
    private String structuredData;
}
