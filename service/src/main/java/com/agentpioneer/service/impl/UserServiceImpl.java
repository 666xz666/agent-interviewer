package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.UserMapper;
import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.AuthBO;
import com.agentpioneer.pojo.bo.UserUpdateBO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Objects;

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
        queryWrapper.or();
        queryWrapper.eq("email", user.getEmail());
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

        user1.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user1);

        return user1;
    }

    @Override
    public User getUserInfo(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public void updateUserInfo(UserUpdateBO userUpdateBO) throws BusinessException {
        Long userId = userUpdateBO.getUserId();
        User user = getUserInfo(userId);
        if (user == null) {
            throw new BusinessException(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        if (Objects.equals(userUpdateBO.getUsername(), "")) {
            throw new BusinessException(ResponseStatusEnum.ADMIN_USERNAME_NULL_ERROR);
        }
        if (userUpdateBO.getUsername() != null && !Objects.equals(userUpdateBO.getUsername(), user.getUsername())) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userUpdateBO.getUsername());
            User existingUser = userMapper.selectOne(queryWrapper);
            if (existingUser != null) {
                throw new BusinessException(ResponseStatusEnum.USERNAME_EXISTS_ERROR);
            }
        }
        if (Objects.equals(userUpdateBO.getEmail(), "")) {
            throw new BusinessException(ResponseStatusEnum.EMAIL_NULL_ERROR);
        }
        if (userUpdateBO.getEmail() != null && !Objects.equals(userUpdateBO.getEmail(), user.getEmail())) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", userUpdateBO.getEmail());
            User existingUser = userMapper.selectOne(queryWrapper);
            if (existingUser != null) {
                throw new BusinessException(ResponseStatusEnum.EMAIL_EXISTS_ERROR);
            }
        }

//        if (userUpdateBO.getPassword() == null) {
//            throw new BusinessException(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
//        }
//        String passwordHashed = encoder.encode(userUpdateBO.getPassword());
//        userUpdateBO.setPassword(passwordHashed);

        BeanUtils.copyProperties(userUpdateBO, user);
        int res = userMapper.updateById(user);

        if (res != 1) {
            throw new BusinessException(ResponseStatusEnum.FAILED);
        }
    }
}