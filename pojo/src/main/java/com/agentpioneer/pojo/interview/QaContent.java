package com.agentpioneer.pojo.interview;

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
public class QaContent {
    String query;
    String answer;
    List<Double> expression; // 表情的概率
    List<Integer> scores;
    List<String> reasons;
}
