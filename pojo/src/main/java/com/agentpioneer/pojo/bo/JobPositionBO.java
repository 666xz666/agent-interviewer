package com.agentpioneer.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JobPositionBO {
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
    private List<String> techStack;
}
