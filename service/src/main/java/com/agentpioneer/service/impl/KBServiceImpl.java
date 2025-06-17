package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.KnowledgeBaseMapper;
import com.agentpioneer.pojo.KnowledgeBase;
import com.agentpioneer.pojo.bo.KnowledgeBaseBO;
import com.agentpioneer.pojo.vo.KnowledgeBaseVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.JobPositionService;
import com.agentpioneer.service.KBService;
import com.agentpioneer.service.MilvusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KBServiceImpl implements KBService {
    @Autowired
    private KnowledgeBaseMapper knowledgeBaseMapper;

    @Autowired
    private MilvusService milvusService;

    @Autowired
    JobPositionService jobPositionService;

    @Override
    public void create(KnowledgeBaseBO knowledgeBaseBO, Long userId) throws BusinessException {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        BeanUtils.copyProperties(knowledgeBaseBO, knowledgeBase);

        Long jobId = knowledgeBase.getJobId();
        if (!jobPositionService.exists(jobId)) {
            throw new BusinessException(ResponseStatusEnum.JOB_NOT_EXIST);
        }

        knowledgeBase.setCreator(userId);

        // 检查知识库名称是否重复
        KnowledgeBase existingKB = new KnowledgeBase();
        existingKB.setKnowledgeName(knowledgeBase.getKnowledgeName());
        QueryWrapper<KnowledgeBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("knowledge_name", knowledgeBase.getKnowledgeName());
        if (knowledgeBaseMapper.selectOne(queryWrapper) != null) {
            throw new BusinessException(ResponseStatusEnum.KB_NAME_EXIST);
        }

        try {
            milvusService.create(knowledgeBase.getKnowledgeName(), knowledgeBase.getDescription());
        } catch (Exception e) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }

        int res = knowledgeBaseMapper.insert(knowledgeBase);
        if (res != 1) {
            milvusService.dropCollect(knowledgeBase.getKnowledgeName());
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }
    }

    public Boolean exists(Long id) {
        if (id == null) {
            return false;
        }
        QueryWrapper<KnowledgeBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("knowledge_id", id);
        return knowledgeBaseMapper.exists(queryWrapper);
    }

    public void delete(Long id) throws BusinessException {
        if (!exists(id)) {
            throw new BusinessException(ResponseStatusEnum.KB_NOT_EXIST);
        }

        KnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(id);
        String knowledgeName = knowledgeBase.getKnowledgeName();
        try {
            milvusService.dropCollect(knowledgeName);
        } catch (Exception e) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }

        int res = knowledgeBaseMapper.deleteById(id);
        if (res != 1) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }
    }

    public List<KnowledgeBaseVO> list(Long jobId) throws BusinessException {
        if (!jobPositionService.exists(jobId)) {
            throw new BusinessException(ResponseStatusEnum.JOB_NOT_EXIST);
        }

        QueryWrapper<KnowledgeBase> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("job_id", jobId);
        List<KnowledgeBase> kbList = knowledgeBaseMapper.selectList(queryWrapper);

        return kbList.stream().map(kb -> {
            KnowledgeBaseVO kbVO = new KnowledgeBaseVO();
            BeanUtils.copyProperties(kb, kbVO);
            return kbVO;
        }).toList();
    }
}
