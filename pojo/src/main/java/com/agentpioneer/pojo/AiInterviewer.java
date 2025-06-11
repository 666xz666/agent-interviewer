package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * AI面试官配置表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@TableName("ai_interviewer")
public class AiInterviewer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * AI面试官ID，主键
     */
    @TableId(value = "interviewer_id", type = IdType.AUTO)
    private Long interviewerId;

    /**
     * 面试官名称（如"技术面试官Alice"）
     */
    private String name;

    /**
     * 性别标识
     */
    private String gender;

    /**
     * 面试官描述（如"专注Java开发岗位的技术面试官"）
     */
    private String description;

    /**
     * API服务ID（关联LLM/语音合成服务）
     */
    private String apiServiceId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    public Long getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiServiceId() {
        return apiServiceId;
    }

    public void setApiServiceId(String apiServiceId) {
        this.apiServiceId = apiServiceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "AiInterviewer{" +
        "interviewerId = " + interviewerId +
        ", name = " + name +
        ", gender = " + gender +
        ", description = " + description +
        ", apiServiceId = " + apiServiceId +
        ", createdAt = " + createdAt +
        ", updatedAt = " + updatedAt +
        "}";
    }
}
