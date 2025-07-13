package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.JobPositionMapper;
import com.agentpioneer.pojo.JobPosition;
import com.agentpioneer.pojo.bo.JobPositionBO;
import com.agentpioneer.pojo.vo.JobPositionVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.JobPositionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.milvus.common.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPositionServiceImpl implements JobPositionService {
    @Autowired
    JobPositionMapper jobPositionMapper;

    public void create(JobPositionBO jobPositionBO, Long userId) throws BusinessException {
        JobPosition jobPosition = new JobPosition();
        BeanUtils.copyProperties(jobPositionBO, jobPosition);
        jobPosition.setTechStack(JsonUtils.toJson(jobPositionBO.getTechStack()));
        jobPosition.setCreatedBy(userId);
        int res = jobPositionMapper.insert(jobPosition);
        if (res != 1) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }
    }

    @Override
    public List<JobPositionVO> listJobPositions() throws BusinessException {
        return jobPositionMapper.listJobPositions();
    }

    @Override
    public Boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        QueryWrapper<JobPosition> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", id);
        return jobPositionMapper.exists(queryWrapper);
    }

    @Override
    public String getJobPrompt(Long jobId) throws BusinessException {
        QueryWrapper<JobPosition> jobPositionQueryWrapper = new QueryWrapper<>();
        jobPositionQueryWrapper.eq("job_id", jobId);
        JobPosition jobPosition = jobPositionMapper.selectOne(jobPositionQueryWrapper);
        if (jobPosition == null) throw new BusinessException(ResponseStatusEnum.FAILED);

        String res = "Job Name: " + jobPosition.getJobName() + "\n";
        res += "Job Description: " + jobPosition.getJobDescription();
        return res;
    }
}
