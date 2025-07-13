package com.agentpioneer.pojo.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum InterviewStatus {
    PROCESSING("PROCESSING", "处理中"),
    SUCCESS("SUCCESS", "处理成功"),
    FAILED("FAILED", "处理失败"),
    CANCELLED("CANCELLED", "已取消");

    @EnumValue          // MyBatis-Plus 将使用该值与数据库交互
    private final String code;

    private final String desc;

    InterviewStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
