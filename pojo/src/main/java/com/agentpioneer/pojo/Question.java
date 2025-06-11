package com.agentpioneer.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 题库表
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 题目ID，主键
     */
    @TableId(value = "question_id", type = IdType.AUTO)
    private Long questionId;

    /**
     * 关联知识库ID
     */
    private Long knowledgeId;

    /**
     * 题目类型
     */
    private String questionType;

    /**
     * 题目内容
     */
    private String questionContent;

    /**
     * 难度等级
     */
    private String difficulty;

    /**
     * 是否由LLM生成：1-是，0-否
     */
    private Byte isGeneratedByLlm;

    /**
     * 创建者ID（关联user表）
     */
    private Long creator;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Byte getIsGeneratedByLlm() {
        return isGeneratedByLlm;
    }

    public void setIsGeneratedByLlm(Byte isGeneratedByLlm) {
        this.isGeneratedByLlm = isGeneratedByLlm;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Question{" +
        "questionId = " + questionId +
        ", knowledgeId = " + knowledgeId +
        ", questionType = " + questionType +
        ", questionContent = " + questionContent +
        ", difficulty = " + difficulty +
        ", isGeneratedByLlm = " + isGeneratedByLlm +
        ", creator = " + creator +
        ", createdAt = " + createdAt +
        "}";
    }
}
