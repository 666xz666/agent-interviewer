package com.agentpioneer.service.impl;

import com.agentpioneer.mapper.InterviewEvaluationMapper;
import com.agentpioneer.mapper.InterviewMapper;
import com.agentpioneer.mapper.InterviewQaMapper;
import com.agentpioneer.mapper.JobPositionMapper;
import com.agentpioneer.pojo.*;
import com.agentpioneer.pojo.bo.*;
import com.agentpioneer.pojo.enums.InterviewQueryType;
import com.agentpioneer.pojo.enums.InterviewStatus;
import com.agentpioneer.pojo.interview.QaContent;
import com.agentpioneer.pojo.vo.InterviewVO;
import com.agentpioneer.pojo.vo.QueryVO;
import com.agentpioneer.pojo.vo.ResumeContentVO;
import com.agentpioneer.result.BusinessException;
import com.agentpioneer.result.ResponseStatusEnum;
import com.agentpioneer.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.github.briqt.spark4j.constant.SparkApiVersion;
import io.github.briqt.spark4j.model.request.function.SparkFunctionBuilder;
import io.milvus.common.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private InterviewEvaluationMapper interviewEvaluationMapper;
    @Autowired
    private JobPositionMapper jobPositionMapper;

    @Autowired
    private UserService userService;

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
    public QueryVO query(
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
                "- Background: 一位用户需要进行面试模拟，以提升面试技巧和表现。用户正在求职，希望在实际面试前通过模拟面试熟悉流程、增强信心并优化回答策略。\n" +
                "- Profile: 你是一位经验丰富的人力资源专家和面试官，对各类岗位的招聘流程和要求有着深刻的理解。你擅长通过提问和对话，挖掘候选人的潜力和不足，同时能够给予建设性的反馈。\n" +
                "- Skills: 你具备出色的沟通能力、敏锐的观察力和专业的评估技巧。能够根据岗位要求设计有针对性的面试问题，并根据候选人的回答进行深入追问和分析。\n";
        systemPrompt += "- Job: \n" + jobPrompt + "\n";

        ChatBO chatBO = new ChatBO();
        chatBO.setHistories(interviewQueryBO.getHistories());
        chatBO.setContent(interviewQueryBO.getContent());

        SparkFunctionBuilder sparkFunctionBuilder =
                SparkFunctionBuilder.functionName("interviewQuery")
                        .description("你必须通过调用此方法与面试者进行交流。")
                        .addParameterProperty("content", "string", "必填，提问或对话的内容")
                        .addParameterProperty("action", "string", "必填，面试官所做的动作，20字以内")
                        .addParameterProperty("thinking", "string", "必填，面试官的思考，100字以内")
                        .addParameterRequired("content", "action", "thinking");

        if (type == InterviewQueryType.GREETING) {
            systemPrompt += "- Task: 面试刚刚开始，你现在需要给面试者打招呼,做自我介绍，并请面试者简单介绍下自己";
        }
        if (type == InterviewQueryType.BASIC) {
            systemPrompt += "- Task: 现在你需要向面试者提出一个岗位相关的专业基础知识问题，从岗位技术栈中选择";
        }
        if (type == InterviewQueryType.RESUME) {
            systemPrompt += "- Task: 现在你需要就面试者简历项目提出一个岗位相关的专业问题";
            systemPrompt += "- Resume: \n";
            ResumeContentVO resumeContentVO = resumeService.get(interview.getResumeId(), userId);
            systemPrompt += JsonUtils.toJson(resumeContentVO);
        }
        if (type == InterviewQueryType.SCENARIO) {
            systemPrompt += "- Task: 现在你需要向面试者提出一个岗位相关的场景题";
        }
        if (type == InterviewQueryType.END) {
            systemPrompt += "- Task: 面试结束，你现在无需继续提问,需要向面试者道别";
        }

        chatBO.setContent(systemPrompt);
        String content = sparkLLMService.chat(
                systemPrompt,
                chatBO,
                3000,
                0.5,
                SparkApiVersion.V3_0
        );

        QueryVO queryVO = new QueryVO();
        queryVO.setContent(content);
        return queryVO;
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
        if (res != 1) throw new BusinessException(ResponseStatusEnum.FAILED);
    }


    @Override
    public void evalInterview(
            Long interviewId
    ) {
        QueryWrapper<Interview> interviewQueryWrapperQueryWrapper = new QueryWrapper<>();
        interviewQueryWrapperQueryWrapper.eq("interview_id", interviewId);
        Interview interview = interviewMapper.selectOne(interviewQueryWrapperQueryWrapper);
        if (interview == null) {
            return;
        }

        interview.setStatus(InterviewStatus.EVALUATING.getCode());
        interviewMapper.updateById(interview);

        try {
            QueryWrapper<InterviewQa> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("interview_id", interviewId)
                    .orderBy(true, true, "qa_order");
            List<InterviewQa> interviewQaList = interviewQaMapper.selectList(queryWrapper);

            String jobPrompt = jobPositionService.getJobPrompt(interview.getJobId());
            String systemPrompt = "- Role: 专业面试官\n" +
                    "- Background: 用户刚刚进行了一场面试，你现在需要对面试环节的一问一答进行评分，并给出相应的评价\n" +
                    "- Profile: 你是一位经验丰富的人力资源专家和面试官，对各类岗位的招聘流程和要求有着深刻的理解。你擅长通过提问和对话，挖掘候选人的潜力和不足，同时能够给予建设性的反馈。\n" +
                    "- Skills: 你具备出色的评价能力，对用户做出的评价有理有据。";
            systemPrompt += "- Job: \n" + jobPrompt + "\n";

            SparkFunctionBuilder sparkFunctionBuilder = SparkFunctionBuilder
                    .functionName("interviewEval")
                    .description("对候选人整场面试表现进行量化评分，返回6个核心维度得分")
                    .addParameterProperty("knowledgeLevel", "int", "专业知识水平，0~100分")
                    .addParameterProperty("skillMatch", "int", "技能匹配度，0~100分")
                    .addParameterProperty("expression", "int", "语言表达能力，0~100分")
                    .addParameterProperty("logic", "int", "逻辑思维能力，0~100分")
                    .addParameterProperty("innovation", "int", "创新能力，0~100分")
                    .addParameterProperty("pressure", "int", "应变抗压能力，0~100分")
                    .addParameterProperty("summary", "string", "综合评语，200字以内")
                    .addParameterRequired(
                            "knowledgeLevel",
                            "skillMatch",
                            "expression",
                            "logic",
                            "innovation",
                            "pressure",
                            "summary"
                    );

            String finalSystemPrompt = systemPrompt;
            List<Map<String, Object>> maps = new ArrayList<>();
            interviewQaList.forEach(qa -> {
                String qaContentString = qa.getQaContent();
                QaContent qaContent = JsonUtils.fromJson(qaContentString, QaContent.class);
                qaContentString += "\n-Task: 调用调用interviewEval进行评价";
                ChatBO chatBO = new ChatBO();
                chatBO.setContent(qaContentString);
                Map<String, Object> map = sparkLLMService.functionCall(
                        sparkFunctionBuilder,
                        finalSystemPrompt,
                        chatBO,
                        3000,
                        0.0,
                        SparkApiVersion.V3_0
                );
                qaContent.setEvalMap(map);
                maps.add(map);
                qa.setQaContent(JsonUtils.toJson(qaContent));
                interviewQaMapper.updateById(qa);
            });

            String qaScoresJson = JsonUtils.toJson(maps);
            String totalSystemPrompt =
                    "- Role: 资深面试官\n" +
                            "- Background: 你手里已有面试过程中每一轮问答（QA）的维度评分。\n" +
                            "- Task: 请基于这些评分，给出整场面试的 **加权/综合** 分数和总结。\n" +
                            "- QA细评分参考:\n" + qaScoresJson + "\n" +
                            "- Job:\n" + jobPrompt + "\n";
            ChatBO totalChatBO = new ChatBO();
            totalChatBO.setContent("请根据以上QA细评分，调用interviewEval输出整场面试的总评分");
            Map<String, Object> totalMap = sparkLLMService.functionCall(
                    sparkFunctionBuilder,
                    totalSystemPrompt,
                    totalChatBO,
                    3500,
                    0.0,
                    SparkApiVersion.V3_0
            );

            InterviewEvaluation interviewEvaluation = new InterviewEvaluation();
            interviewEvaluation.setInterviewId(interviewId);
            interviewEvaluation.setContent(JsonUtils.toJson(totalMap));
            int res = interviewEvaluationMapper.insert(interviewEvaluation);
            if (res == 0) throw new BusinessException(ResponseStatusEnum.FAILED);

            interview.setStatus(InterviewStatus.COMPLETED.getCode());
            interviewMapper.updateById(interview);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            interview.setStatus(InterviewStatus.FAILED.getCode());
            interviewMapper.updateById(interview);
        }
    }

    @Override
    public List<InterviewVO> list(Long userId) {
        QueryWrapper<Interview> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Interview> interviewList = interviewMapper.selectList(queryWrapper);

        List<InterviewVO> interviewVOList = new ArrayList<>();
        interviewList.forEach(interview -> {
            InterviewVO interviewVO = new InterviewVO();
            BeanUtils.copyProperties(interview, interviewVO);

            Long jobId = interview.getJobId();
            JobPosition jobPosition = jobPositionMapper.selectById(jobId);
            interviewVO.setJobName(jobPosition.getJobName());

            Long resumeId = interview.getResumeId();
            ResumeContentVO resumeContentVO = resumeService.get(resumeId, userId);
            interviewVO.setResumeName(resumeContentVO.getOriginalFileName());
            interviewVOList.add(interviewVO);
        });

        return interviewVOList;
    }
}
