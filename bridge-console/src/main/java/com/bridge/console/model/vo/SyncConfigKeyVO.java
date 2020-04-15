package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 同步配置项至其他环境的实体类
 * @date 2020-03-04 15:47
 */
@Data
public class SyncConfigKeyVO {

    /**
     * 环境id
     */
    private Integer envId;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 配置项k/v
     */
    private List<ConfigKvVO> configKvVOList;
}
