package com.agentpioneer.controller;

import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.UserUpdateBO;
import com.agentpioneer.pojo.vo.UserVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.SslBundleSslEngineFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Tag(name = "用户接口", description = "用户信息相关接口")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @Operation(summary = "获取用户信息", description = "获取当前登录用户的信息")
    @GetMapping("/info")
    public GraceJSONResult userInfo(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromCookie(request);

        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return GraceJSONResult.ok(userVO);
    }


    @Operation(summary = "更新用户信息", description = "更新当前登录用户的信息")
    @PostMapping("/update")
    public GraceJSONResult updateUser(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "用户更新信息, 不更新的字段可以不写",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "用户更新示例",
                                            summary = "用户更新请求体示例",
                                            value = "{\"username\": \"testUser\", \"email\": \"test@example.com\"}"
                                    ),
                                    @ExampleObject(
                                            name = "只更新邮箱示例",
                                            summary = "只更新邮箱的请求体示例",
                                            value = "{\"email\": \"test@example.com\"}"
                                    )
                            }
                    )
            )
            UserUpdateBO userUpdateBO,
            HttpServletRequest request
    ) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        userUpdateBO.setUserId(userId);

        // 实现更新逻辑
        try {
            userService.updateUserInfo(userUpdateBO);
        } catch (BusinessException e) {
            System.out.println(e.getStatus());
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }

        return GraceJSONResult.ok();
    }
}
