package com.agentpioneer.pojo;

import com.agentpioneer.pojo.enums.ResumeStatus;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户简历表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
public class Resume implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 简历ID，主键
     */
    @TableId(value = "resume_id", type = IdType.AUTO)
    private Long resumeId;

    /**
     * 用户ID，关联user表
     */
    private Long userId;

    /**
     * 原始简历文件名（如"张三_简历.pdf"）
     */
    private String originalFileName;

    /**
     * 结构化简历数据（个人信息、教育背景等）
     */
    private String structuredData;

    /**
     * 异步处理状态
     */
    private String status;

    /**
     * RabbitMQ任务ID
     */
    private String rabbitmqJobId;

    /**
     * 处理开始时间
     */
    private LocalDateTime processingStartTime;

    /**
     * 处理结束时间
     */
    private LocalDateTime processingEndTime;

    /**
     * 处理失败时的错误信息
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(String structuredData) {
        this.structuredData = structuredData;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRabbitmqJobId() {
        return rabbitmqJobId;
    }

    public void setRabbitmqJobId(String rabbitmqJobId) {
        this.rabbitmqJobId = rabbitmqJobId;
    }

    public LocalDateTime getProcessingStartTime() {
        return processingStartTime;
    }

    public void setProcessingStartTime(LocalDateTime processingStartTime) {
        this.processingStartTime = processingStartTime;
    }

    public LocalDateTime getProcessingEndTime() {
        return processingEndTime;
    }

    public void setProcessingEndTime(LocalDateTime processingEndTime) {
        this.processingEndTime = processingEndTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
        return "Resume{" +
        "resumeId = " + resumeId +
        ", userId = " + userId +
        ", originalFileName = " + originalFileName +
        ", structuredData = " + structuredData +
        ", status = " + status +
        ", rabbitmqJobId = " + rabbitmqJobId +
        ", processingStartTime = " + processingStartTime +
        ", processingEndTime = " + processingEndTime +
        ", errorMessage = " + errorMessage +
        ", createdAt = " + createdAt +
        ", updatedAt = " + updatedAt +
        "}";
    }
}
