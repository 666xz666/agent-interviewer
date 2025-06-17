package com.agentpioneer.service.impl;

import com.agentpioneer.pojo.bo.ResumeUpdateBO;
import com.agentpioneer.pojo.enums.ResumeStatus;
import com.agentpioneer.mapper.ResumeMapper;
import com.agentpioneer.pojo.Resume;
import com.agentpioneer.pojo.bo.ResumeBO;
import com.agentpioneer.pojo.vo.ResumeContentVO;
import com.agentpioneer.pojo.vo.ResumeVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.ResumeService;
import com.agentpioneer.utils.JsonUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
public class ResumeImpl implements ResumeService {


    @Autowired
    ResumeMapper resumeMapper;

    @Override
    public void create(ResumeBO resumeBO, Long userId) throws BusinessException {
        // 实现创建简历的逻辑
        Resume resume = new Resume();
        resume.setProcessingStartTime(LocalDateTime.now());

        resume.setUserId(userId);
        String resumeContent = JsonUtils.objectToJson(resumeBO);
        resume.setStructuredData(resumeContent);
        resume.setOriginalFileName(resumeBO.getOriginalFileName());
        resume.setStatus(ResumeStatus.SUCCESS.toString());

        resume.setProcessingEndTime(LocalDateTime.now());

        int res = resumeMapper.insert(resume);
        if (res != 1) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }
    }

    @Override
    public ArrayList<ResumeVO> list(Long userId) throws BusinessException {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Resume> resumes = resumeMapper.selectList(queryWrapper);
        ArrayList<ResumeVO> resumeVOArrayList = new ArrayList<>();
        for (Resume resume : resumes) {
            ResumeVO resumeVO = new ResumeVO();
            BeanUtils.copyProperties(resume, resumeVO);
            resumeVOArrayList.add(resumeVO);
        }
        return resumeVOArrayList;
    }

    @Override
    public ResumeContentVO get(Long resumeId, Long userId) throws BusinessException {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resume_id", resumeId)
                .eq("user_id", userId);
        Resume resume = resumeMapper.selectOne(queryWrapper);
        if (resume == null) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }
        if (resume.getStatus().equals(ResumeStatus.PROCESSING.toString())) {
            throw new BusinessException(ResponseStatusEnum.RESUME_PROCESSING);
        }
        ResumeContentVO resumeContentVO = new ResumeContentVO();
        BeanUtils.copyProperties(resume, resumeContentVO);
        return resumeContentVO;
    }

    public void delete(Long resumeId, Long userId) throws BusinessException {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resume_id", resumeId)
                .eq("user_id", userId);
        Resume resume = resumeMapper.selectOne(queryWrapper);
        if (resume == null) {
            throw new BusinessException(ResponseStatusEnum.RESUME_NOT_FOUND);
        }
        if (resume.getStatus().equals(ResumeStatus.PROCESSING.toString())) {
            throw new BusinessException(ResponseStatusEnum.RESUME_PROCESSING);
        }
        resumeMapper.deleteById(resumeId);
    }

    @Override
    public void update(ResumeUpdateBO resumeUpdateBO, Long userId) throws BusinessException {
        Long resumeId = resumeUpdateBO.getResumeId();
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resume_id", resumeId)
                .eq("user_id", userId);
        Resume resume = resumeMapper.selectOne(queryWrapper);
        if (resume == null) {
            throw new BusinessException(ResponseStatusEnum.RESUME_NOT_FOUND);
        }
        if (resume.getStatus().equals(ResumeStatus.PROCESSING.toString())) {
            throw new BusinessException(ResponseStatusEnum.RESUME_PROCESSING);
        }
        BeanUtils.copyProperties(resumeUpdateBO, resume);
        resumeMapper.updateById(resume);
    }
}
