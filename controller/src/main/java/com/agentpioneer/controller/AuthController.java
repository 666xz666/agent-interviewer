package com.agentpioneer.controller;

import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.AuthBO;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "认证接口", description = "用户登录、注册相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public GraceJSONResult register(
            @RequestBody AuthBO authBO
            ) {
        boolean res = userService.register(authBO);
        if(res) return GraceJSONResult.ok();
        else return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_REGISTER_ERROR);
    }

    @PostMapping("/login")
    public GraceJSONResult login(
            @RequestBody AuthBO authBO,
            HttpServletResponse response
    ){
        User user = userService.validate(authBO);
        if(user == null) return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);

        Long userId = user.getUserId();
        JwtUtil.generateTokenAndSetCookie(userId, response);
        return GraceJSONResult.ok();
    }

    @PostMapping("/session")
    public GraceJSONResult session(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        if(userId == null) return GraceJSONResult.errorCustom(ResponseStatusEnum.TICKET_INVALID);
        User user = userService.getUserInfo(userId);
        return GraceJSONResult.ok(userId);
    }
}
