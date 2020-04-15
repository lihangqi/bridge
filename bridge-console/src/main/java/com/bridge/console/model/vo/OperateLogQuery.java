package com.bridge.console.model.vo;

import com.bridge.console.utils.result.BasePageQueryParam;
import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-03-07 13:44
 */
@Data
public class OperateLogQuery extends BasePageQueryParam {

    /**
     * 环境id
     */
    private Integer envId;

    /**
     * key
     */
    private String configKey;


    /**
     * 应用名称
     */
    private String appName;


    /*================================查询使用，前端不需要传的===============================*/


    /**
     * 应用id
     */
    private List<Integer> appIdList;

}
