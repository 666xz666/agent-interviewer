package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.AiInterviewerMapper;
import com.agentpioneer.pojo.AiInterviewer;
import com.agentpioneer.service.InterviewerService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InterviewerServiceImpl implements InterviewerService {
    @Autowired
    AiInterviewerMapper aiInterviewerMapper;

    @Override
    public List<AiInterviewer> list() {
        QueryWrapper<AiInterviewer> queryWrapper = new QueryWrapper<>();
        return aiInterviewerMapper.selectList(queryWrapper);
    }
}
