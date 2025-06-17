package com.agentpioneer.pojo.resume;

import lombok.Data;

import java.io.Serializable;

@Data
public class WorkExperienceStruct implements Serializable {
    private String company;         // 公司名称
    private String position;        // 职位
    private String description;     // 工作描述
    private String startDate;       // 开始时间
    private String endDate;         // 结束时间
}
