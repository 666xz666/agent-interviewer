package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 面试问答环节表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@TableName("interview_qa")
public class InterviewQa implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问答ID，主键
     */
    @TableId(value = "qa_id", type = IdType.AUTO)
    private Long qaId;

    /**
     * 面试ID，关联interview表
     */
    private Long interviewId;

    /**
     * 问答顺序
     */
    private Integer qaOrder;

    /**
     * 问答类型
     */
    private String qaType;

    /**
     * 问答内容
     */
    private String qaContent;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public Long getQaId() {
        return qaId;
    }

    public void setQaId(Long qaId) {
        this.qaId = qaId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Integer getQaOrder() {
        return qaOrder;
    }

    public void setQaOrder(Integer qaOrder) {
        this.qaOrder = qaOrder;
    }

    public String getQaType() {
        return qaType;
    }

    public void setQaType(String qaType) {
        this.qaType = qaType;
    }

    public String getQaContent() {
        return qaContent;
    }

    public void setQaContent(String qaContent) {
        this.qaContent = qaContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InterviewQa{" +
        "qaId = " + qaId +
        ", interviewId = " + interviewId +
        ", qaOrder = " + qaOrder +
        ", qaType = " + qaType +
        ", qaContent = " + qaContent +
        ", createdAt = " + createdAt +
        "}";
    }
}
