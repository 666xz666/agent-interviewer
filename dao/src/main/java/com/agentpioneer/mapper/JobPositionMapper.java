package com.agentpioneer.mapper;

import com.agentpioneer.pojo.JobPosition;
import com.agentpioneer.pojo.vo.JobPositionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 岗位信息表 Mapper 接口
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
public interface JobPositionMapper extends BaseMapper<JobPosition> {
    List<JobPositionVO> listJobPositions();
}
