package com.agentpioneer.controller;

import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.KnowledgeBaseBO;
import com.agentpioneer.pojo.vo.JobPositionVO;
import com.agentpioneer.pojo.vo.KnowledgeBaseVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.JobPositionService;
import com.agentpioneer.service.KBService;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kb")
@Tag(
        name = "知识库接口",
        description = "知识库相关接口"
)
public class KBController {
    @Autowired
    UserService userService;

    @Autowired
    KBService kbService;

    @PostMapping("create")
    @Operation(
            summary = "创建知识库 [管理员]",
            description = "创建知识库， 需要管理员权限"
    )
    public GraceJSONResult create(
            @RequestBody
            @Parameter(
                    name = "知识库创建请求体",
                    description = "知识库信息"
            )
            KnowledgeBaseBO knowledgeBaseBO,
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
            kbService.create(knowledgeBaseBO, userId);
            return GraceJSONResult.ok();
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }


    @DeleteMapping("delete")
    @Operation(
            summary = "删除知识库 [管理员]",
            description = "删除知识库， 需要管理员权限"
    )
    public GraceJSONResult delete(
            @RequestParam("id")
            Long id,
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
            kbService.delete(id);
            return GraceJSONResult.ok();
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

    @GetMapping("list")
    @Operation(
            summary = "查询指定岗位的知识库列表",
            description = "查询指定岗位的知识库列表"
    )
    public GraceJSONResult list(
            @RequestParam("jobId")
            Long jobId
    ) {
        try {
            List<KnowledgeBaseVO> kbVOS = kbService.list(jobId);
            return GraceJSONResult.ok(kbVOS);
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }
}
