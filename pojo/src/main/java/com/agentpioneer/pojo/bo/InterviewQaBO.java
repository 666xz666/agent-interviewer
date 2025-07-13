package com.agentpioneer.pojo.bo;

import com.agentpioneer.pojo.enums.InterviewQueryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterviewQaBO {
    InterviewQueryType type;
    String query;
    String answer;
    List<Double> expression;
    Long interviewId;
}
