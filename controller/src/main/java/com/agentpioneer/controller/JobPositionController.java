package com.agentpioneer.controller;

import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.JobPositionBO;
import com.agentpioneer.pojo.vo.JobPositionVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.JobPositionService;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/jobPosition")
@Tag(
        name = "岗位接口",
        description = "岗位相关接口"
)
public class JobPositionController {
    @Autowired
    UserService userService;

    @Autowired
    JobPositionService jobPositionService;

    @PostMapping("/create")
    @Operation(summary = "创建岗位 [管理员权限]", description = "创建岗位, 管理员可以创建岗位")
    public GraceJSONResult createJobPosition(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "岗位信息",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "岗位创建示例",
                                            summary = "岗位创建请求体示例",
                                            value = "{\n" +
                                                    "\"jobName\": \"Java后端开发工程师\",\n" +
                                                    "\"jobDescription\": \"负责公司业务系统的设计与开发，参与架构设计和技术难题攻关，确保系统的稳定运行和性能优化。具体包括根据系统研发需求制定后端开发方案并编码实现，完成产品模块的详细设计与代码实现及单元测试，配合测试团队进行系统测试、性能调优等工作。与团队成员紧密合作，推动系统整合与应用优化，持续关注技术趋势，提升团队技术水平。\",\n" +
                                                    "\"techStack\": [\n" +
                                                    "\"Java（精通语法、面向对象编程、多线程、网络编程等）\",\n" +
                                                    "\"Spring框架（Spring Boot、Spring Cloud、Spring MVC等）\",\n" +
                                                    "\"数据库（MySQL、Oracle等关系型数据库，Redis、MongoDB等非关系型数据库）\",\n" +
                                                    "\"中间件（RocketMQ、RabbitMQ、Kafka等消息队列，Nacos等服务注册与发现组件）\",\n" +
                                                    "\"版本控制（Git）\",\n" +
                                                    "\"构建工具（Maven、Gradle）\",\n" +
                                                    "\"容器化（Docker、Kubernetes）\",\n" +
                                                    "\"Linux（熟悉常用命令、具备部署和日志分析等能力）\"\n" +
                                                    "]\n" +
                                                    "}"
                                    )
                            }
                    )
            )
            JobPositionBO jobPositionBO,
            HttpServletRequest request
    ) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }
        if (user.getRole().equals("user")) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.NO_AUTH);
        }

        try {
            jobPositionService.create(jobPositionBO, userId);
            return GraceJSONResult.ok();
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

    @GetMapping("/list")
    @Operation(summary = "获取岗位列表", description = "获取岗位列表")
    public GraceJSONResult listJobPosition() {
        try {
            List<JobPositionVO> jobPositionVOList = jobPositionService.listJobPositions();
            return GraceJSONResult.ok(jobPositionVOList);
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

}
