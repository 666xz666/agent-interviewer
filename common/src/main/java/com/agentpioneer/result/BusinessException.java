package com.agentpioneer.result;

import com.agentpioneer.result.ResponseStatusEnum;

/**
 * 业务异常类，用于封装业务层面的错误信息
 */
public class BusinessException extends RuntimeException {
    private final ResponseStatusEnum status;

    public BusinessException(ResponseStatusEnum status) {
        super(status.msg());
        this.status = status;
    }

    public ResponseStatusEnum getStatus() {
        return status;
    }
}
