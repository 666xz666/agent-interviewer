package com.agentpioneer.pojo.resume;

import lombok.Data;

import java.io.Serializable;

@Data
public class EducationStruct implements Serializable {
    private String school;          // 学校
    private String degree;          // 学历
    private String major;           // 专业
    private String startDate;       // 开始时间
    private String endDate;         // 结束时间
}
