package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试总评价表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@TableName("interview_evaluation")
public class InterviewEvaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总评价ID，主键
     */
    @TableId(value = "evaluation_id", type = IdType.AUTO)
    private Long evaluationId;

    /**
     * 面试ID，关联interview表
     */
    private Long interviewId;

    /**
     * 评估结构化内容
     */
    private String content;

    /**
     * 评价生成时间
     */
    private LocalDateTime createdAt;

    public Long getEvaluationId() {
        return evaluationId;
    }

    public void setEvaluationId(Long evaluationId) {
        this.evaluationId = evaluationId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InterviewEvaluation{" +
        "evaluationId = " + evaluationId +
        ", interviewId = " + interviewId +
        ", content = " + content +
        ", createdAt = " + createdAt +
        "}";
    }
}
