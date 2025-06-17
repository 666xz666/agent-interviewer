package com.agentpioneer.controller;

import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.ResumeBO;
import com.agentpioneer.pojo.bo.ResumeUpdateBO;
import com.agentpioneer.pojo.vo.ResumeContentVO;
import com.agentpioneer.pojo.vo.ResumeVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.ResumeService;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Tag(
        name = "简历模块",
        description = "简历相关接口"
)
@RestController
@RequestMapping("/resume")
public class ResumeController {
    @Autowired
    UserService userService;

    @Autowired
    ResumeService resumeService;

    @PostMapping("/create")
    @Operation(
            summary = "创建简历",
            description = "创建简历"

    )
    public GraceJSONResult create(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "创建简历示例",
                                            summary = "创建简历请求体示例",
                                            value = "{\n" +
                                                    "  \"originalFileName\": \"许战\",\n" +
                                                    "  \"baseInfo\": {\n" +
                                                    "    \"firstName\": \"战\",\n" +
                                                    "    \"lastName\": \"许\",\n" +
                                                    "    \"gender\": \"\",\n" +
                                                    "    \"age\": 0,\n" +
                                                    "    \"phone\": \"155-0519-3862\",\n" +
                                                    "    \"email\": \"15505193862@163.com\",\n" +
                                                    "    \"location\": \"\"\n" +
                                                    "  },\n" +
                                                    "  \"educationList\": [\n" +
                                                    "    {\n" +
                                                    "      \"school\": \"中国矿业大学\",\n" +
                                                    "      \"degree\": \"本科\",\n" +
                                                    "      \"major\": \"计算机科学与技术\",\n" +
                                                    "      \"startDate\": \"2022-07\",\n" +
                                                    "      \"endDate\": \"至今\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"workExperienceList\": [\n" +
                                                    "    {\n" +
                                                    "      \"company\": \"南京智图科技有限公司\",\n" +
                                                    "      \"position\": \"后端开发实习生\",\n" +
                                                    "      \"description\": \"参与基于RAG和知识图谱的企业知识库问答系统开发，使用FastAPI搭建后端接口，结合Vue3实现前端交互界面；负责企业文档数据的清洗、切片及向量化处理，运用检索增强生成技术提升问答准确率；协助构建知识图谱，通过微调大语言模型抽取关系三元组，优化子图检索策略，使系统响应速度提升30%。\",\n" +
                                                    "      \"startDate\": \"2023-07\",\n" +
                                                    "      \"endDate\": \"2023-09\"\n" +
                                                    "    },\n" +
                                                    "    {\n" +
                                                    "      \"company\": \"苏州星途智能科技有限公司\",\n" +
                                                    "      \"position\": \"算法实习工程师\",\n" +
                                                    "      \"description\": \"参与空间目标检测算法优化项目，基于Faster RCNN框架替换主干网络为HiViT模型，引入Transformer块重构检测头；针对星上资源受限问题，采用数据增强策略扩充数据集，结合级联激活掩码和注意力机制提升模型推理效率；通过STD方法解耦参数预测，使检测精度提升12%，模型参数量减少40%。\",\n" +
                                                    "      \"startDate\": \"2024-01\",\n" +
                                                    "      \"endDate\": \"2024-03\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            }
                    )
            )
            ResumeBO resumeBO,
            HttpServletRequest request
    ) {
        Long userId = JwtUtil.getUserIdFromCookie(request);

        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        try {
            resumeService.create(resumeBO, userId);

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
            summary = "获取简历列表",
            description = "获取简历列表"
    )
    public GraceJSONResult list(HttpServletRequest request) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        try {
            ArrayList<ResumeVO> resumeVOArrayList = resumeService.list(userId);
            return GraceJSONResult.ok(resumeVOArrayList);
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

    @GetMapping("/info")
    @Operation(
            summary = "获取简历内容",
            description = "获取简历内容"
    )
    public GraceJSONResult info(
            @RequestParam("resumeId") Long resumeId,
            HttpServletRequest request
    ) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        try {
            ResumeContentVO resumeContentVO = resumeService.get(resumeId, userId);
            return GraceJSONResult.ok(resumeContentVO);
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "删除简历",
            description = "删除简历"
    )
    public GraceJSONResult delete(
            @RequestParam("resumeId") Long resumeId,
            HttpServletRequest request
    ) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }
        try {
            resumeService.delete(resumeId, userId);
            return GraceJSONResult.ok();
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

    @PostMapping("/update")
    @Operation(
            summary = "更新简历",
            description = "更新简历"
    )
    public GraceJSONResult update(
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "更新简历示例",
                                            summary = "更新简历请求体示例",
                                            value = "{\n" +
                                                    "  \"resumeId\": 1,\n" +
                                                    "  \"originalFileName\": \"许战\",\n" +
                                                    "  \"baseInfo\": {\n" +
                                                    "    \"firstName\": \"战\",\n" +
                                                    "    \"lastName\": \"许\",\n" +
                                                    "    \"gender\": \"\",\n" +
                                                    "    \"age\": 0,\n" +
                                                    "    \"phone\": \"155-0519-3862\",\n" +
                                                    "    \"email\": \"15505193862@163.com\",\n" +
                                                    "    \"location\": \"\"\n" +
                                                    "  },\n" +
                                                    "  \"educationList\": [\n" +
                                                    "    {\n" +
                                                    "      \"school\": \"中国矿业大学\",\n" +
                                                    "      \"degree\": \"本科\",\n" +
                                                    "      \"major\": \"计算机科学与技术\",\n" +
                                                    "      \"startDate\": \"2022-07\",\n" +
                                                    "      \"endDate\": \"至今\"\n" +
                                                    "    }\n" +
                                                    "  ],\n" +
                                                    "  \"workExperienceList\": [\n" +
                                                    "    {\n" +
                                                    "      \"company\": \"南京智图科技有限公司\",\n" +
                                                    "      \"position\": \"后端开发实习生\",\n" +
                                                    "      \"description\": \"参与基于RAG和知识图谱的企业知识库问答系统开发，使用FastAPI搭建后端接口，结合Vue3实现前端交互界面；负责企业文档数据的清洗、切片及向量化处理，运用检索增强生成技术提升问答准确率；协助构建知识图谱，通过微调大语言模型抽取关系三元组，优化子图检索策略，使系统响应速度提升30%。\",\n" +
                                                    "      \"startDate\": \"2023-07\",\n" +
                                                    "      \"endDate\": \"2023-09\"\n" +
                                                    "    },\n" +
                                                    "    {\n" +
                                                    "      \"company\": \"苏州星途智能科技有限公司\",\n" +
                                                    "      \"position\": \"算法实习工程师\",\n" +
                                                    "      \"description\": \"参与空间目标检测算法优化项目，基于Faster RCNN框架替换主干网络为HiViT模型，引入Transformer块重构检测头；针对星上资源受限问题，采用数据增强策略扩充数据集，结合级联激活掩码和注意力机制提升模型推理效率；通过STD方法解耦参数预测，使检测精度提升12%，模型参数量减少40%。\",\n" +
                                                    "      \"startDate\": \"2024-01\",\n" +
                                                    "      \"endDate\": \"2024-03\"\n" +
                                                    "    }\n" +
                                                    "  ]\n" +
                                                    "}"
                                    )
                            }
                    )
            )
            ResumeUpdateBO resumeUpdateBO,
            HttpServletRequest request
    ) {
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }
        try {
            resumeService.update(resumeUpdateBO, userId);
            return GraceJSONResult.ok();
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }


}
