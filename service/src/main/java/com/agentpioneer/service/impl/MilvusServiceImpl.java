package com.agentpioneer.service.impl;

import com.agentpioneer.service.MilvusService;
import com.google.common.collect.Lists;
import io.milvus.client.MilvusServiceClient;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.grpc.DataType;
import io.milvus.grpc.GetLoadStateResponse;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.response.SearchResultsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// @ https://blog.csdn.net/withme977/article/details/136840199

@Slf4j
@Service
public class MilvusServiceImpl implements MilvusService {

    @Autowired
    private MilvusServiceClient milvusServiceClient;


    final IndexType INDEX_TYPE = IndexType.IVF_FLAT;   // IndexType
    final String INDEX_PARAM = "{\"nlist\":1024}";     // ExtraParam

    /**
     * 创建集合的字段
     * text_id  对应的文本id
     * vector  向量字段
     * file_id  标签
     */
    private final String TEXT_ID = "text_id";
    private final String VECTOR = "vector";
    private final String FILE_ID = "file_id";

    private final int dimension = 2;


    /**
     * 创建集合  指定集合名称
     */
    @Override
    public void create(String collectionName, String desc) {
        log.info("Miluvs create collectionName:{}, desc:{}", collectionName, desc);
        boolean has = hasCollect(collectionName);
        log.info("Miluvs hasCollect:{}", has);
        // 不存在此集合才进行创建集合
        if (!has) {
            //  创建集合 设置索引 加载集合到内存中
            FieldType fieldType1 = FieldType.newBuilder()
                    .withName(TEXT_ID)
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build();
            FieldType fieldType2 = FieldType.newBuilder()
                    .withName(VECTOR)  // 设置向量名称
                    .withDataType(DataType.FloatVector)  // 设置向量类型
                    .withDimension(dimension) // 设置向量维度
                    .build();
            FieldType fieldType3 = FieldType.newBuilder()
                    .withName(FILE_ID)
                    .withDataType(DataType.Int64)
                    .build();
            CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withDescription(desc)
                    .withShardsNum(2)
                    .addFieldType(fieldType1)
                    .addFieldType(fieldType2)
                    .addFieldType(fieldType3)
                    .withEnableDynamicField(true)
                    .withConsistencyLevel(ConsistencyLevelEnum.BOUNDED)
                    .build();
            R<RpcStatus> response = milvusServiceClient.createCollection(createCollectionReq);
            if (response.getStatus() != R.Status.Success.getCode()) {
                log.info("milvus create fail message:{}", response.getMessage());
            } else {
                // 创建集合索引并加载集合到内存  插入数据和搜索的前置操作！！
                createIndex(collectionName);
            }
        }
    }


    /**
     * 创建集合索引 -- 加在向量字段上
     *
     * @param collectionName
     */
    public void createIndex(String collectionName) {
        milvusServiceClient.createIndex(
                CreateIndexParam.newBuilder()
                        .withCollectionName(collectionName)
                        .withFieldName(VECTOR)
                        .withIndexType(INDEX_TYPE)
                        .withMetricType(MetricType.L2)
                        .withExtraParam(INDEX_PARAM)
                        .withSyncMode(Boolean.FALSE)
                        .build()
        );
        // 加载所创建的集合
        loadCollection(collectionName);

    }


    /**
     * 加载集合
     *
     * @param collectionName
     */
    public void loadCollection(String collectionName) {
        milvusServiceClient.loadCollection(
                LoadCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
        // You can check the loading status
        GetLoadStateParam param = GetLoadStateParam.newBuilder()
                .withCollectionName(collectionName)
                .build();
        R<GetLoadStateResponse> stateResponse = milvusServiceClient.getLoadState(param);
        if (stateResponse.getStatus() != R.Status.Success.getCode()) {
            System.out.println(stateResponse.getMessage());
        }
    }


    /**
     * 集合是否存在
     *
     * @return
     */
    @Override
    public Boolean hasCollect(String collectionName) {
        R<Boolean> hasResult = milvusServiceClient.hasCollection(
                HasCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build());
        if (hasResult.getStatus() == R.Status.Success.getCode()) {
            return hasResult.getData();
        }
        return false;
    }


    /**
     * 向量库中插入数据（）
     */
    @Override
    public Boolean insert(String name, List<Long> textIds, List<List<Float>> vectorList, List<Long> fileIdList) {
        log.info("milvus insert name:{}, textIds:{}, vectorList:{}", name, textIds, vectorList);
//        List<Long> file_idList = new ArrayList<>();
//        for (Long textId : textIds) {
//            file_idList.add(0L);
//        }
        List<InsertParam.Field> fieldsInsert = new ArrayList<>();
        fieldsInsert.add(new InsertParam.Field(TEXT_ID, textIds));  // 文本对应的ids数据list
        fieldsInsert.add(new InsertParam.Field(VECTOR, vectorList));  // 转换后的向量数据list
        fieldsInsert.add(new InsertParam.Field(FILE_ID, fileIdList));  // 标签占位符  给个0
        InsertParam param = InsertParam.newBuilder()
                .withCollectionName(name)
                .withFields(fieldsInsert)
                .build();
        R<MutationResult> response = milvusServiceClient.insert(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            log.info("milvus insert vector fail! message:{}", response.getMessage());
            return false;
        } else {
            return true;
        }

    }


    /**
     * 删除集合(知识库)
     *
     * @param collectionName
     */
    @Override
    public void dropCollect(String collectionName) {
        milvusServiceClient.dropCollection(
                DropCollectionParam.newBuilder()
                        .withCollectionName(collectionName)
                        .build()
        );
    }


    /**
     * 根据文件id删除向量
     *
     * @param collectionName
     * @param fileIds
     */
    @Override
    public void dropVectors(String collectionName, List<Long> fileIds) {
        String expr = FILE_ID + " in " + fileIds;
        DeleteParam param = DeleteParam.newBuilder()
                .withCollectionName(collectionName)
                .withExpr(expr)
                .build();
        R<MutationResult> response = milvusServiceClient.delete(param);
        if (response.getStatus() != R.Status.Success.getCode()) {
            System.out.println(response.getMessage());
        }
    }


    /**
     * 向量搜索 - 向量库中用具体向量搜索 - 返回indexIds
     */
    @Override
    public List<Long> search(String collectionName, int topK, List<List<Float>> vectorList) {
        // 构建查询条件  进行向量字段查询   待测试1024维度向量
        SearchParam searchParam = io.milvus.param.dml.SearchParam.newBuilder()
                .withCollectionName(collectionName)
                .withVectorFieldName(VECTOR)
                .withOutFields(Lists.newArrayList("*"))
                .withVectors(vectorList)
                .withTopK(topK)
                .build();
        R<SearchResults> searchResults = milvusServiceClient.search(searchParam);
        if (searchResults.getStatus() != R.Status.Success.getCode()) {
            log.info(searchResults.getMessage());
        }
        List<Long> textIdList = new ArrayList<>();
        SearchResultsWrapper wrapper = new SearchResultsWrapper(searchResults.getData().getResults());
        for (int i = 0; i < vectorList.size(); ++i) {
            List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(i);
            for (SearchResultsWrapper.IDScore score : scores) {
                Map<String, Object> filedsMap = score.getFieldValues();
                textIdList.add(Long.valueOf(String.valueOf(filedsMap.get(TEXT_ID))));
            }
        }
        return textIdList;
    }


    /**
     * 删除集合中的 id对应的向量
     */
//    public void deleteEmbedingById(){
//        List<String> ids = Lists.newArrayList("441966745769900131","441966745769900133");
//        DeleteIdsParam param = DeleteIdsParam.newBuilder()
//                .withCollectionName(FaceArchive.COLLECTION_NAME_MILVUS_TESTONE)
//                .withPrimaryIds(ids)
//                .build();
//        R<DeleteResponse> response = milvusServiceClient.delete(param);
//        if (response.getStatus() != R.Status.Success.getCode()) {
//            System.out.println(response.getMessage());
//        }
//
//        for (Object deleteId : response.getData().getDeleteIds()) {
//            System.out.println(deleteId);
//        }
//
//    }


//    // 测试用的向量数据类型
//    public List<List<Float>> getListVector() {
//        List<Float> vectorData = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            vectorData.add((float) Math.random());
//        }
//        List<List<Float>> vectors = new ArrayList<>();
//        vectors.add(vectorData);
//
//        return vectors;
//    }
}