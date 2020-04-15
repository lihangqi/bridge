package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 发布相关
 * @date 2019-02-21 14:49
 */
@Data
public class PushConfigKvVO {

    /**
     * 记录id
     */
    private Integer id;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 需要发布的实例
     */
    private List<String> machineList;

    /**
     * 发布类型{@link com.bridge.console.model.enums.PushTypeEnum}
     */
    private Integer pushType;
}
