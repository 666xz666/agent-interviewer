package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.UserMapper;
import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.AuthBO;
import com.agentpioneer.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean register(AuthBO authBO) {
        if (authBO == null || authBO.getUsername() == null || authBO.getPassword() == null) {
            return false;
        }

        // 实现注册逻辑
        User user = new User();
        BeanUtils.copyProperties(authBO, user);
        user.setUsername(user.getUsername());

        String password =  user.getPassword();
        String passwordHashed = encoder.encode(password);
        user.setPassword(passwordHashed);

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
        if (
                authBO == null ||
                (authBO.getUsername() == null && authBO.getEmail() == null) ||
                authBO.getPassword() == null
        ) {
            return null;
        }

        User user = new User();
        BeanUtils.copyProperties(authBO, user);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("username", user.getUsername())
                                          .or()
                                          .eq("email", user.getEmail()));
        User user1 = userMapper.selectOne(queryWrapper);

        if (!encoder.matches(user.getPassword(), user1.getPassword())) {
            return null;
        }

        return user1;
    }

    @Override
    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }
}
