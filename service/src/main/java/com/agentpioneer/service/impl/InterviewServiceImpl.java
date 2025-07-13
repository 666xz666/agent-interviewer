package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.InterviewMapper;
import com.agentpioneer.mapper.InterviewQaMapper;
import com.agentpioneer.pojo.Interview;
import com.agentpioneer.pojo.InterviewQa;
import com.agentpioneer.pojo.bo.ChatBO;
import com.agentpioneer.pojo.bo.InterviewBO;
import com.agentpioneer.pojo.bo.InterviewQaBO;
import com.agentpioneer.pojo.bo.InterviewQueryBO;
import com.agentpioneer.pojo.enums.InterviewQueryType;
import com.agentpioneer.pojo.enums.InterviewStatus;
import com.agentpioneer.pojo.interview.QaContent;
import com.agentpioneer.pojo.vo.ResumeContentVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.InterviewService;
import com.agentpioneer.service.JobPositionService;
import com.agentpioneer.service.ResumeService;
import com.agentpioneer.service.SparkLLMService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.milvus.common.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InterviewServiceImpl implements InterviewService {
    @Autowired
    InterviewMapper interviewMapper;

    @Autowired
    SparkLLMService sparkLLMService;

    @Autowired
    JobPositionService jobPositionService;

    @Autowired
    ResumeService resumeService;
    @Autowired
    private InterviewQaMapper interviewQaMapper;

    @Override
    public Interview createInterview(
            InterviewBO interviewBO,
            Long userId
    ) throws BusinessException {
        Interview interview = new Interview();
        BeanUtils.copyProperties(interviewBO, interview); // 简历，面试官
        interview.setStartTime(LocalDateTime.now()); // 开始时间
        interview.setStatus(InterviewStatus.PROCESSING.getCode()); // 进入处理状态
        interview.setUserId(userId); // 用户ID

        int res = interviewMapper.insert(interview); // 插入SQL
        if (res == 0) throw new BusinessException(ResponseStatusEnum.FAILED); // 处理插入失败

        return interview;
    }

    @Override
    public Flux<ServerSentEvent<GraceJSONResult>> query(
            InterviewQueryBO interviewQueryBO,
            Long userId
    ) throws BusinessException {
        InterviewQueryType type = interviewQueryBO.getType();

        Long interviewId = interviewQueryBO.getInterviewId();
        QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interview_id", interviewId);
        Interview interview = interviewMapper.selectOne(queryWrapper);
        if (interview == null) throw new BusinessException(ResponseStatusEnum.FAILED);

        String jobPrompt = jobPositionService.getJobPrompt(interview.getJobId());
        String systemPrompt = "- Role: 专业面试官\n" +
                "- Background: 用户需要进行面试模拟，以提升面试技巧和表现。用户可能正在求职，希望在实际面试前通过模拟面试熟悉流程、增强信心并优化回答策略。\n" +
                "- Profile: 你是一位经验丰富的人力资源专家和面试官，对各类岗位的招聘流程和要求有着深刻的理解。你擅长通过提问和对话，挖掘候选人的潜力和不足，同时能够给予建设性的反馈。\n" +
                "- Skills: 你具备出色的沟通能力、敏锐的观察力和专业的评估技巧。能够根据岗位要求设计有针对性的面试问题，并根据候选人的回答进行深入追问和分析。";
        systemPrompt += "- Job: \n" + jobPrompt + "\n";

        ChatBO chatBO = new ChatBO();
        chatBO.setHistories(interviewQueryBO.getHistories());
        if (type == InterviewQueryType.GREETING) {
            systemPrompt += "- Task: 面试刚刚开始，你现在需要给面试者打招呼,做自我介绍，并请面试者简单介绍下自己";
            chatBO.setContent("面试官你好");
            return sparkLLMService.chatStream(
                    systemPrompt,
                    chatBO,
                    3000,
                    0.5,
                    SparkApiVersion.V3_0
            );
        }
        if (type == InterviewQueryType.BASIC) {
            systemPrompt += "- Task: 现在你需要向面试者提出一个岗位相关的专业问题";
            return sparkLLMService.chatStream(
                    systemPrompt,
                    chatBO,
                    3000,
                    0.5,
                    SparkApiVersion.V3_0
            );
        }
        if (type == InterviewQueryType.RESUME) {
            systemPrompt += "- Task: 现在你需要就面试者简历项目提出一个岗位相关的专业问题";

            systemPrompt += "- Resume: \n";
            ResumeContentVO resumeContentVO = resumeService.get(interview.getResumeId(), userId);
            systemPrompt += JsonUtils.toJson(resumeContentVO);

            return sparkLLMService.chatStream(
                    systemPrompt,
                    chatBO,
                    3000,
                    0.5,
                    SparkApiVersion.V3_0
            );
        }
        if (type == InterviewQueryType.SCENARIO) {
            systemPrompt += "- Task: 现在你需要向面试者提出一个岗位相关的场景题";
            return sparkLLMService.chatStream(
                    systemPrompt,
                    chatBO,
                    3000,
                    0.5,
                    SparkApiVersion.V3_0
            );
        }
        if (type == InterviewQueryType.END) {
            systemPrompt += "- Task: 面试结束，你现在需要向面试者道别";
            return sparkLLMService.chatStream(
                    systemPrompt,
                    chatBO,
                    3000,
                    0.5,
                    SparkApiVersion.V3_0
            );
        }

        return null;
    }

    @Override
    public void saveQA(
            InterviewQaBO interviewQaBO
    ) throws BusinessException {
        InterviewQa interviewQa = new InterviewQa();
        interviewQa.setInterviewId(interviewQaBO.getInterviewId()); // interviewId
        interviewQa.setQaType(interviewQaBO.getType().toString()); // QaType

        Long interviewId = interviewQaBO.getInterviewId();
        QueryWrapper<InterviewQa> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interview_id", interviewId);
        List<InterviewQa> interviewQaList = interviewQaMapper.selectList(queryWrapper);
        int num = interviewQaList.size();
        interviewQa.setQaOrder(num); // QaOrder

        QaContent qaContent = new QaContent();
        BeanUtils.copyProperties(interviewQaBO, qaContent);
        interviewQa.setQaContent(JsonUtils.toJson(qaContent)); // QaContent

        int res = interviewQaMapper.insert(interviewQa);
        if (res == 0) throw new BusinessException(ResponseStatusEnum.FAILED);
    }
}
