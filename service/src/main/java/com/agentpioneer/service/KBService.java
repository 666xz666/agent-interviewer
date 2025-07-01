package com.agentpioneer.service;


import com.agentpioneer.pojo.bo.KnowledgeBaseBO;
import com.agentpioneer.pojo.vo.KnowledgeBaseVO;
import com.agentpioneer.result.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KBService {
    void create(KnowledgeBaseBO knowledgeBaseBO, Long userId) throws BusinessException;

    Boolean exists(Long id);

    void delete(Long id) throws BusinessException;

    List<KnowledgeBaseVO> list(Long jobId) throws BusinessException;

//    void uploadFiles(MultipartFile[] files, Long kbId, Long userId) throws BusinessException;
}
