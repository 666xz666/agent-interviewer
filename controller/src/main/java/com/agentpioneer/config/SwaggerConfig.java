package com.agentpioneer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("多模态智能面试体API文档")
                        .version("1.0")
                        .description("后端API文档")
                        .termsOfService("http://swagger.io/terms/") // 修正为纯文本 URL
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))); // 修正为纯文本 URL
    }
}