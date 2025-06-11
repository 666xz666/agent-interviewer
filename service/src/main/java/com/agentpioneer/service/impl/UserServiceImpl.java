package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.UserMapper;
import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.AuthBO;
import com.agentpioneer.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean register(AuthBO authBO) {
        // 实现注册逻辑
        User user = new User();
        BeanUtils.copyProperties(authBO, user);
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        user.setEmail(user.getEmail());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User user1 = userMapper.selectOne(queryWrapper);
        if (user1 != null) return false;

        int result = userMapper.insert(user);
        return result == 1;
    }

    @Override
    public User validate(AuthBO authBO) {
        User user = new User();
        BeanUtils.copyProperties(authBO, user);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("password", user.getPassword())
                    .and(wrapper -> wrapper.eq("username", user.getUsername())
                                          .or()
                                          .eq("email", user.getEmail()));
        User user1 = userMapper.selectOne(queryWrapper);

        return user1;
    }

    @Override
    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }
}
