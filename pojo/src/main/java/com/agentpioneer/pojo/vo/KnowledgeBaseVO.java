package com.agentpioneer.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeBaseVO {
    private Long knowledgeId;

    /**
     * 知识库名称
     */
    private String knowledgeName;

    /**
     * 知识库描述
     */
    private String description;
}
