package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 配置页面选择应用的二连联动筛选列表
 * @date 2019-01-28 14:47
 */
@Data
public class ConfigSelectorVO {


    /**
     * 团队id
     */
    private Integer teamId;


    /**
     * 团队名称
     */
    private String teamName;


    /**
     * app的列表
     */
    List<ConfigAppVO> configAppList;
}
