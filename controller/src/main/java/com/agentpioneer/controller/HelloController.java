package com.agentpioneer.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * @ Description: 测试路由
     * @ return
     */
    @GetMapping("hello")
    public Object hello() {
        return "hello pioneer";
    }
}
