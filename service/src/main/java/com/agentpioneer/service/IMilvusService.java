package com.agentpioneer.service;

import java.util.List;

/**
 * milvus向量数据库相关业务接口
 *
 * @author Jx
 * @version 2024-3-18
 */
public interface IMilvusService {
    Boolean hasCollect(String collectionName);

    void create(String collectionName, String desc);

    Boolean insert(String name, List<Long> textIds, List<List<Float>> vectorList, List<Long> fileIdList);

    List<Long> search(String name, int topK, List<List<Float>> vectorList);

    void dropCollect(String name);

    void createIndex(String name);

    void dropVectors(String name, List<Long> fileIds);
}