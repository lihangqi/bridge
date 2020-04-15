package com.bridge.console.model.vo;

import com.bridge.console.utils.result.BasePageQueryParam;
import lombok.Data;


/**
 * @author Jay
 * @version v1.0
 * @description 应用查询VO
 * @date 2019-01-25 15:48
 */
@Data
public class AppListQuery extends BasePageQueryParam {


    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用编码
     */
    private String appCode;

    /**
     * teamId
     */
    private Integer teamId;

    /**
     * 系统负责人
     */
    private Integer appOwner;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 环境id
     */
    private Integer envId;
}
