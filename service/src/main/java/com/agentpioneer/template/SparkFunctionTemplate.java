package com.agentpioneer.template;

import io.github.briqt.spark4j.model.request.function.SparkFunctionBuilder;
import io.github.briqt.spark4j.model.request.function.SparkRequestFunctionMessage;
import io.github.briqt.spark4j.model.request.function.SparkRequestFunctionParameters;
import io.github.briqt.spark4j.model.request.function.SparkRequestFunctionProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 星火大模型函数调用模板类
 * 用于构建和管理函数定义
 */
public class SparkFunctionTemplate {

    private final SparkFunctionBuilder builder;

    public SparkFunctionTemplate(String name, String description) {
        this.builder = SparkFunctionBuilder.functionName(name)
                .description(description);
    }

    // 添加字符串类型的参数
    public SparkFunctionTemplate addStringParameter(String name, String description, boolean required) {
        builder.addParameterProperty(name, "string", description);
        if (required) {
            builder.addParameterRequired(name);
        }
        return this;
    }

    // 添加整数类型的参数
    public SparkFunctionTemplate addIntegerParameter(String name, String description, boolean required) {
        builder.addParameterProperty(name, "integer", description);
        if (required) {
            builder.addParameterRequired(name);
        }
        return this;
    }

    // 添加布尔类型的参数
    public SparkFunctionTemplate addBooleanParameter(String name, String description, boolean required) {
        builder.addParameterProperty(name, "boolean", description);
        if (required) {
            builder.addParameterRequired(name);
        }
        return this;
    }

    // 添加对象类型的参数
    public SparkFunctionTemplate addObjectParameter(String name, String description, boolean required,
                                                    Map<String, ParameterDefinition> properties) {
        Map<String, SparkRequestFunctionProperty> sparkProperties = new HashMap<>();
        properties.forEach((propName, prop) -> {
            sparkProperties.put(propName, new SparkRequestFunctionProperty(prop.type, prop.description));
        });

        SparkRequestFunctionParameters objectParam = new SparkRequestFunctionParameters();
        objectParam.setType("object");
        objectParam.setProperties(sparkProperties);

        builder.addParameterProperty(name, "object", description);
        if (required) {
            builder.addParameterRequired(name);
        }
        return this;
    }

    // 添加数组类型的参数
    public SparkFunctionTemplate addArrayParameter(String name, String description, boolean required,
                                                   String itemsType) {
        builder.addParameterProperty(name, "array", description);
        if (required) {
            builder.addParameterRequired(name);
        }
        return this;
    }

    // 构建SparkRequestFunctionMessage对象
    public SparkRequestFunctionMessage build() {
        return builder.build();
    }

    // 参数定义内部类
    public static class ParameterDefinition {
        private String type;
        private String description;

        public ParameterDefinition(String type, String description) {
            this.type = type;
            this.description = description;
        }
    }
}