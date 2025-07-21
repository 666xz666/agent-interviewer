package com.agentpioneer.controller;

import com.agentpioneer.result.GraceJSONResult;
import com.agentpioneer.service.InterviewerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "AI面试官",
        description = "面试官相关接口"
)
@RestController
@RequestMapping("interviewer")
public class InterviewerController {
    @Autowired
    InterviewerService interviewerService;

    @GetMapping("list")
    @Operation(
            summary = "列出面试官",
            description = "列出所有的面试官"
    )
    public GraceJSONResult list() {
        return GraceJSONResult.ok(interviewerService.list());
    }
}
