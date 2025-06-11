package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 岗位知识库主表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@TableName("knowledge_base")
public class KnowledgeBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 知识库ID，主键
     */
    @TableId(value = "knowledge_id", type = IdType.AUTO)
    private Long knowledgeId;

    /**
     * 关联岗位ID
     */
    private Long jobId;

    /**
     * 知识库名称
     */
    private String knowledgeName;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 创建者ID（关联user表）
     */
    private Long creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 状态：1-启用，0-禁用
     */
    private Byte status;

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getKnowledgeName() {
        return knowledgeName;
    }

    public void setKnowledgeName(String knowledgeName) {
        this.knowledgeName = knowledgeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "KnowledgeBase{" +
        "knowledgeId = " + knowledgeId +
        ", jobId = " + jobId +
        ", knowledgeName = " + knowledgeName +
        ", description = " + description +
        ", creator = " + creator +
        ", createTime = " + createTime +
        ", updateTime = " + updateTime +
        ", status = " + status +
        "}";
    }
}
