package com.agentpioneer.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobPositionVO {
    private Long jobId;

    /**
     * 岗位名称
     */
    private String jobName;

    /**
     * 岗位描述
     */
    private String jobDescription;

    /**
     * 技术栈要求（JSON格式）
     */
    private String techStack;

    private String creatorName;
}
