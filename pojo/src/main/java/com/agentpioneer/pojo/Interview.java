package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试基本信息表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-07-12
 */
public class Interview implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 面试ID，主键
     */
    @TableId(value = "interview_id", type = IdType.AUTO)
    private Long interviewId;

    /**
     * 用户ID，关联user表
     */
    private Long userId;

    /**
     * 岗位ID，关联job_position表
     */
    private Long jobId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 面试状态
     */
    private String status;

    /**
     * 总评价任务ID（消息队列）
     */
    private String rabbitmqTotalJobId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 指定的AI面试官ID（关联ai_interviewer表）
     */
    private Long interviewerId;

    /**
     * 使用的简历ID
     */
    private Long resumeId;

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRabbitmqTotalJobId() {
        return rabbitmqTotalJobId;
    }

    public void setRabbitmqTotalJobId(String rabbitmqTotalJobId) {
        this.rabbitmqTotalJobId = rabbitmqTotalJobId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getInterviewerId() {
        return interviewerId;
    }

    public void setInterviewerId(Long interviewerId) {
        this.interviewerId = interviewerId;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    @Override
    public String toString() {
        return "Interview{" +
        "interviewId = " + interviewId +
        ", userId = " + userId +
        ", jobId = " + jobId +
        ", startTime = " + startTime +
        ", endTime = " + endTime +
        ", status = " + status +
        ", rabbitmqTotalJobId = " + rabbitmqTotalJobId +
        ", createdAt = " + createdAt +
                ", interviewerId = " + interviewerId +
        "}";
    }
}
