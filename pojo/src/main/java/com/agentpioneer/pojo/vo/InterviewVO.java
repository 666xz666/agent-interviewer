package com.agentpioneer.pojo.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterviewVO {
    private Long interviewId;

    /**
     * 岗位ID，关联job_position表
     */
    private Long jobId;

    private String jobName;

    /**
     * 使用的简历ID
     */
    private Long resumeId;

    private String resumeName;

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
     * 指定的AI面试官ID（关联ai_interviewer表）
     */
    private Long interviewerId;


}
