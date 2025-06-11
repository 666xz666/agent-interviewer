package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 岗位信息表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@TableName("job_position")
public class JobPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 岗位ID，主键
     */
    @TableId(value = "job_id", type = IdType.AUTO)
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

    /**
     * 创建者ID（关联user表）
     */
    private Long createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 状态：1-启用，0-禁用
     */
    private Byte status;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "JobPosition{" +
        "jobId = " + jobId +
        ", jobName = " + jobName +
        ", jobDescription = " + jobDescription +
        ", techStack = " + techStack +
        ", createdBy = " + createdBy +
        ", createdAt = " + createdAt +
        ", updatedAt = " + updatedAt +
        ", status = " + status +
        "}";
    }
}
