package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 配置项删除的集合
 * @date 2020-01-25 01:18
 */
@Data
public class ConfigKeyDelVO {


    /**
     * 配置项key的集合
     */
    private List<Integer> idList;
}
