package com.agentpioneer.controller;

import com.agentpioneer.pojo.Interview;
import com.agentpioneer.pojo.User;
import com.agentpioneer.pojo.bo.EvalBO;
import com.agentpioneer.pojo.bo.InterviewBO;
import com.agentpioneer.pojo.bo.InterviewQaBO;
import com.agentpioneer.pojo.bo.InterviewQueryBO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.InterviewService;
import com.agentpioneer.service.UserService;
import com.agentpioneer.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


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
        if (user.getRole().equals("user")) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.NO_AUTH);
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
    public Flux<ServerSentEvent<GraceJSONResult>> query(
            InterviewQueryBO interviewQueryBO,
            HttpServletRequest request
    ) {
        // 鉴权
        Long userId = JwtUtil.getUserIdFromCookie(request);
        User user = userService.getUserInfo(userId);
        if (user == null) {
            return Flux.just(
                    ServerSentEvent.builder(
                            GraceJSONResult.errorCustom(ResponseStatusEnum.PAYMENT_USER_INFO_ERROR)
                    ).build()
            );
        }
        if (user.getRole().equals("user")) {
            return Flux.just(
                    ServerSentEvent.builder(
                            GraceJSONResult.errorCustom(ResponseStatusEnum.NO_AUTH)
                    ).build()
            );
        }

        try {
            return interviewService.query(interviewQueryBO, user.getUserId());
        } catch (BusinessException e) {
            return Flux.just(
                    ServerSentEvent.builder(
                            GraceJSONResult.errorCustom(e.getStatus())
                    ).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.just(
                    ServerSentEvent.builder(
                            GraceJSONResult.error()
                    ).build()
            );
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

        return GraceJSONResult.error();
    }

    @Operation(
            summary = "评估面试",
            description = "将评估任务加入消息队列中"
    )
    @PostMapping("eval")
    public GraceJSONResult eval(
            EvalBO evalBO,
            HttpServletRequest request
    ) {

    }
}
