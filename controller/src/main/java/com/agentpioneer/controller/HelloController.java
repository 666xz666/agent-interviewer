package com.agentpioneer.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HelloController {

    /**
     * @ Description: 测试路由
     * @ return
     */
    @Operation(
        summary = "测试路由",
        description = "这是一个用于测试的路由接口，返回欢迎信息。",
        tags = {"测试接口"}
    )
    @Parameter(
        name = "name",
        in = ParameterIn.QUERY,
        description = "可选的用户名参数",
        schema = @Schema(type = "string")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功获取欢迎信息", content = @Content(schema = @Schema(type = "string"))),
        @ApiResponse(responseCode = "400", description = "请求参数错误", content = @Content),
        @ApiResponse(responseCode = "500", description = "服务器内部错误", content = @Content)
    })
    @GetMapping("hello")
    public Object hello(@RequestParam(required = false) String name) {
        if (name != null) {
            return "Hello " + name + "! Welcome to pioneer.";
        }
        return "hello pioneer";
    }
}