package com.bridge.console.model.enums;

import com.bridge.console.utils.result.ServiceError;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-21 21:11
 */
public enum PermissionEnum implements ServiceError {

    // 认证失败
    VALIDATE_ERROR(-10086, "认证失败"),
    // 业务处理异常
    PERMISSION_ERROR(-10087, "权限不够,无法操作"),
    ;

    private final int code;
    private final String message;

    PermissionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 错误码
     *
     * @return 返回错误码
     */
    @Override
    public int getCode() {
        return code;
    }

    /**
     * 返回错误信息
     *
     * @return 错误信息
     */
    @Override
    public String getMessage() {
        return message;
    }
}
