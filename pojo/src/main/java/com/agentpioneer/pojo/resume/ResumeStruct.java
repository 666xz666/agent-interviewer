package com.agentpioneer.pojo.resume;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ResumeStruct implements Serializable {
    private BaseInfoStruct baseInfo;
    private ArrayList<EducationStruct> educationList;
    private ArrayList<WorkExperienceStruct> workExperienceList;
}
