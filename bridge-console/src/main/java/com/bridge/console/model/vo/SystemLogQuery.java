package com.bridge.console.model.vo;

import com.bridge.console.utils.result.BasePageQueryParam;
import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-09-04 23:57
 */
@Data
public class SystemLogQuery extends BasePageQueryParam {

    /**
     * 环境
     */
    private Integer envId;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 系统编码
     */
    private List<String> appCodeList;
}
