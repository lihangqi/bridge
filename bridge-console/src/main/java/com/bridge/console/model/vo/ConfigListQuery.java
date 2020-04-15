package com.bridge.console.model.vo;

import com.bridge.console.utils.result.BasePageQueryParam;
import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-29 15:21
 */
@Data
public class ConfigListQuery extends BasePageQueryParam {


    /**
     * 应用id
     */
    private Integer appId;


    /**
     * key
     */
    private String configKey;


    /**
     * 环境
     */
    private Integer envId;

    /**
     * key的状态 {@link com.bridge.console.model.enums.KeyStatusEnum}
     */
    private Integer keyStatus;
}
