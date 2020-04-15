package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 请添加类描述
 * @date 2019-03-11 20:23
 */
@Data
public class WorkSpaceVO {

    /**
     * 项目数
     */
    private Integer projectNumber;

    /**
     * 发布次数
     */
    private Integer pushCount;

    /**
     * 应用列表
     */
    private List<AppDefVO> appDefVoList;
}
