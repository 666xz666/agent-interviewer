package com.agentpioneer.service;

import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.AuthBO;
import com.agentpioneer.pojo.bo.UserUpdateBO;
import org.springframework.stereotype.Service;
import com.agentpioneer.result.BusinessException;

public interface UserService {
    boolean register(AuthBO authBO);
    User validate(AuthBO authBO);
    User getUserInfo(Long userId);
    void updateUserInfo(UserUpdateBO userUpdateBO) throws BusinessException;
}
