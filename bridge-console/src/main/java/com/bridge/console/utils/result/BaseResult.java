package com.bridge.console.utils.result;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-21 21:11
 */
public class BaseResult implements Serializable {
    private static final long serialVersionUID = -4205541359679710511L;
    protected static final Integer SUCCESS_CODE= BaseBizEnum.OK.getCode();
    /**
     * 是否成功
     */
    protected Boolean success;

    /**
     * 返回码
     */
    protected Integer code;

    /**
     * 返回信息
     */
    protected String message;
    /**
     * 后台开发人员提示信息，方便问题的跟踪
     */
    protected String devMsg ;

    /**
     * 是否成功
     * @return true成功，false失败
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * 设置是否成功
     * @param success false：失败，true：成功
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 返回码
     * @return 返回码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置返回码
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 返回信息
     * @return 可能是成功提示信息，也可能是错误提示信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置信息
     * @param message 信息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *  返回开发人员的提示信息
     * @return 信息
     */
    public String getDevMsg() {
        return devMsg;
    }

    /**
     * 设置开发人员提示信息
     * @param devMsg
     */
    public void setDevMsg(String devMsg) {
        this.devMsg = devMsg;
    }


}
