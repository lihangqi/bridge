package com.bridge.exception;


import com.bridge.enums.BaseError;

/**
 * @author Jay
 * @version v1.0
 * @description 执行异常类
 * @date 2018-03-28 14:22
 */
public class BridgeProcessFailException extends RuntimeException {

    private final int errorCode;

    public BridgeProcessFailException(final BaseError errors) {
        super(errors.getMessage());
        this.errorCode = errors.getCode();
    }

    public BridgeProcessFailException(final Integer errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
}
