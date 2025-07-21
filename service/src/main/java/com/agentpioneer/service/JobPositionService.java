package com.agentpioneer.service;

import com.agentpioneer.pojo.JobPosition;
import com.agentpioneer.pojo.bo.JobPositionBO;
import com.agentpioneer.pojo.vo.JobPositionVO;
import com.agentpioneer.result.BusinessException;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface JobPositionService {
    public void create(JobPositionBO jobPositionBO, Long userId) throws BusinessException;

    public List<JobPositionVO> listJobPositions() throws BusinessException;

    public Boolean exists(Long id);

    public String getJobPrompt(Long jobId) throws BusinessException;

    public JobPosition info(Long id) throws BusinessException;
}
