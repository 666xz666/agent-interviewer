package com.agentpioneer.pojo.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class KnowledgeBaseBO {

    /**
     * 知识库名称
     */
    private String knowledgeName;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 关联岗位ID
     */
    private Long jobId;
}
