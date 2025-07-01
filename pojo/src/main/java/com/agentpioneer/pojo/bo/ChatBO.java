package com.agentpioneer.pojo.bo;

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
public class ChatBO {
    String content;
    List<History> histories;
}
