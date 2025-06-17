package com.agentpioneer.pojo.resume;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseInfoStruct implements Serializable {
    private String firstName;       // 名
    private String lastName;        // 姓
    private String gender;          // 性别
    private Integer age;            // 年龄
    private String phone;           // 电话
    private String email;           // 邮箱
    private String location;        // 所在地
}


