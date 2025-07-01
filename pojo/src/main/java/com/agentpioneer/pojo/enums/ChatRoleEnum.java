package com.agentpioneer.pojo.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChatRoleEnum {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    private final String value;

    ChatRoleEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
