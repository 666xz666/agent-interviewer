package com.agentpioneer.pojo.bo;

import com.agentpioneer.pojo.enums.InterviewQueryType;
import com.agentpioneer.pojo.xunfei.History;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InterviewQueryBO {
    Long interviewId;
    InterviewQueryType type;
    String content;
    List<History> histories;
}
