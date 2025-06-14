package com.agentpioneer.controller;


import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.AuthBO;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "认证接口", description = "用户登录、注册、cookie验证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "用户通过用户名、邮箱和密码注册, 三项信息必须填写")
    @PostMapping("/register")
    public GraceJSONResult register(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "用户注册信息",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "用户注册示例",
                                            summary = "用户注册请求体示例",
                                            value = "{\"username\": \"testUser\", \"email\": \"test@example.com\", \"password\": \"Test1234\"}"
                                    )
                            }
                    )
            )
            AuthBO authBO
    ) {
        boolean res = userService.register(authBO);
        if (res) return GraceJSONResult.ok();
        else return GraceJSONResult.errorCustom(ResponseStatusEnum.USER_REGISTER_ERROR);
    }

    @Operation(summary = "用户登录", description = "用户通过用户名或邮箱登录")
    @PostMapping("/login")
    public GraceJSONResult login(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "用户登录信息",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "使用邮箱登录示例",
                                            summary = "使用邮箱登录请求体示例",
                                            value = "{\"email\": \"2965962144@qq.com\", \"password\": \"985211Xz\"}"
                                    ),
                                    @ExampleObject(
                                            name = "使用用户名登录示例",
                                            summary = "使用用户名登录请求体示例",
                                            value = "{\"username\": \"xzxzxz\", \"password\": \"985211Xz\"}"
                                    )
                            }
                    )
            )
            AuthBO authBO,
            HttpServletResponse response
    ) {
        User user = userService.validate(authBO);
        if (user == null) return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);

        Long userId = user.getUserId();
        JwtUtil.generateTokenAndSetCookie(userId, response);
        return GraceJSONResult.ok();
    }

    @Operation(summary = "会话验证", description = "验证cookie")
    @PostMapping("/session")
    public GraceJSONResult session(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        if (userId == null) return GraceJSONResult.errorCustom(ResponseStatusEnum.TICKET_INVALID);
        User user = userService.getUserInfo(userId);
        return GraceJSONResult.ok(userId);
    }
}