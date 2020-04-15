package com.bridge.console.utils.ex;

import com.bridge.console.utils.result.ServiceError;

/**
 * @author Jay
 * @version v1.0
 * @description 异常
 * @date 2019-01-21 14:08
 */
public class BusinessCheckFailException extends RuntimeException {

    private static final long serialVersionUID = -346427066798778452L;
    private final int errorCode;

    public BusinessCheckFailException(final ServiceError errors) {
        super(errors.getMessage());
        this.errorCode = errors.getCode();
    }

    public BusinessCheckFailException(final Integer errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

}