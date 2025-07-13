package com.agentpioneer.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterviewBO {
    Long interviewerId;
    Long resumeId;
    Long KnowledgeBaseId;
}
