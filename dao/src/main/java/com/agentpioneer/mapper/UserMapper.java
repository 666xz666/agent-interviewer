package com.agentpioneer.mapper;

import com.agentpioneer.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户及管理员统一表 Mapper 接口
 * </p>
 *
 * @author agentpioneer
 * @since 2025-06-11
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
