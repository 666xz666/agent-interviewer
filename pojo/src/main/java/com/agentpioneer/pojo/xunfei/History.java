package com.agentpioneer.pojo.xunfei;

import com.agentpioneer.pojo.enums.ChatRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class History {
    ChatRoleEnum role;
    String content;
}
