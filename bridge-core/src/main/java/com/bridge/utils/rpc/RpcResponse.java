package com.bridge.utils.rpc;

import com.bridge.domain.Constants;
import lombok.Data;

import java.io.Serializable;

/**
 * @description: 返回result对象
 * @author: Courser
 * @date: 2017/3/15
 * @version: V1.0
 */
@Data
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = -7647570604845078925L;

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
    protected String devMsg;

    /**
     * 成功返回的数据
     */
    private T result;


    public static <D> RpcResponse<D> wrapSuccessfulResult(D data) {
        RpcResponse<D> result = new RpcResponse<>();
        result.result = data;
        result.success = true;
        result.code = Constants.SUCCESS;
        return result;
    }
}
