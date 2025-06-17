package com.agentpioneer.pojo.bo;

import com.agentpioneer.pojo.resume.BaseInfoStruct;
import com.agentpioneer.pojo.resume.EducationStruct;
import com.agentpioneer.pojo.resume.WorkExperienceStruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ResumeBO {
    private String originalFileName;
    private BaseInfoStruct baseInfo;
    private ArrayList<EducationStruct> educationList;
    private ArrayList<WorkExperienceStruct> workExperienceList;
}
