package com.agentpioneer.service;


import com.agentpioneer.pojo.bo.ResumeBO;
import com.agentpioneer.pojo.bo.ResumeUpdateBO;
import com.agentpioneer.pojo.vo.ResumeContentVO;
import com.agentpioneer.pojo.vo.ResumeVO;
import com.agentpioneer.result.BusinessException;

import java.util.ArrayList;

public interface ResumeService {
    public void create(ResumeBO resumeBO, Long userId) throws BusinessException;

    public ArrayList<ResumeVO> list(Long userId) throws BusinessException;

    public ResumeContentVO get(Long resumeId, Long userId) throws BusinessException;

    public void delete(Long resumeId, Long userId) throws BusinessException;

    public void update(ResumeUpdateBO resumeUpdateBO, Long userId) throws BusinessException;
}

