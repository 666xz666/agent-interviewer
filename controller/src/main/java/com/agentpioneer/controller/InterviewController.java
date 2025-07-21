package com.agentpioneer.controller;

import com.agentpioneer.mapper.InterviewMapper;
import com.agentpioneer.pojo.Interview;
import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.EvalBO;
import com.agentpioneer.pojo.bo.InterviewBO;
import com.agentpioneer.pojo.bo.InterviewQaBO;
import com.agentpioneer.pojo.bo.InterviewQueryBO;
import com.agentpioneer.pojo.vo.QueryVO;
import com.agentpioneer.producer.InterviewEvaluationProducer;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.InterviewService;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Objects;


@Tag(
        name = "面试",
        description = "面试相关接口"
)
@RestController
@RequestMapping("interview")
public class InterviewController {
    @Autowired
    InterviewService interviewService;

    @Autowired
    UserService userService;

    @Autowired
    private InterviewEvaluationProducer interviewEvaluationProducer;


    @Operation(
            summary = "创建面试"
    )
    @PostMapping("create")
    public GraceJSONResult create(
            InterviewBO interviewBO,
            HttpServletRequest request
    ) {
        // 鉴权
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        // 创建面试
        Interview interview = null;
        try {
            interview = interviewService.createInterview(interviewBO, user.getUserId());
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
        return GraceJSONResult.ok(interview);
    }

    @Operation(
            summary = "面试官提问",
            description = "让面试官提出不同类型的问题"
    )
    @PostMapping("query")
    public GraceJSONResult query(
            @RequestBody
            InterviewQueryBO interviewQueryBO,
            HttpServletRequest request
    ) {
        // 鉴权
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }

        try {
            QueryVO queryVO = interviewService.query(interviewQueryBO, user.getUserId());
            return GraceJSONResult.ok(queryVO);
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }
    }

    @Operation(
            summary = "保存一轮对话",
            description = "保存一问一答, \n表情数组用来存每种的概率： 0：其他(非人脸表情图片)\n" +
                    "            1：其他表情\n" +
                    "            2：喜悦\n" +
                    "            3：愤怒\n" +
                    "            4：悲伤\n" +
                    "            5：惊恐\n" +
                    "            6：厌恶\n" +
                    "            7：中性"
    )
    @PostMapping("saveQa")
    public GraceJSONResult saveQa(
            @RequestBody
            InterviewQaBO interviewQaBO,
            HttpServletRequest request
    ) {
        try {
            interviewService.saveQA(interviewQaBO);
        } catch (BusinessException e) {
            return GraceJSONResult.errorCustom(e.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return GraceJSONResult.error();
        }

        return GraceJSONResult.ok();
    }

    @Autowired
    InterviewMapper interviewMapper;

    @Operation(
            summary = "评估面试",
            description = "将评估任务加入消息队列中"
    )
    @PostMapping("eval")
    public GraceJSONResult eval(
            @RequestBody
            EvalBO evalBO,
            HttpServletRequest request
    ) {
        Long interviewId = evalBO.getInterviewId();
        Interview interview = interviewMapper.selectById(interviewId);
        if (interview == null) return GraceJSONResult.error();
        if (Objects.equals(interview.getStatus(), "EVALUATING")) return GraceJSONResult.ok();

        interviewEvaluationProducer.sendEvaluationTask(interviewId);
        return GraceJSONResult.ok();
    }

    @Operation(
            summary = "列出面试记录",
            description = "列出所有的面试记录，包括其元信息，以及处于什么状态('PROCESSING', 'EVALUATING', 'COMPLETED', 'FAILED')"
    )
    @GetMapping("list")
    public GraceJSONResult list(HttpServletRequest request) {
        // 鉴权
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR);
        }
        return GraceJSONResult.ok(interviewService.list(user.getUserId()));
    }
}
