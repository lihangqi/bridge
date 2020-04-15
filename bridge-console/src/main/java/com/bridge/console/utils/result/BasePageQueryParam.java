package com.bridge.console.utils.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-01-21 21:11
 */
@Setter
@Getter
public class BasePageQueryParam {
    /**
     * 排序
     */
    private List<String> sorts;
    /**
     * 限制条数
     */
    private Integer limit;
    /**
     * 限制开始条数
     */
    private Integer offset;
}
