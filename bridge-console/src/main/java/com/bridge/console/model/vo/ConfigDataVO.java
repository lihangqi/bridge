package com.bridge.console.model.vo;

import lombok.Data;

import java.util.List;


/**
 * @author Jay
 * @version v1.0
 * @description 配置数据
 * @date 2019-01-29 15:06
 */
@Data
public class ConfigDataVO {

    /**
     * <pre>
     *
     * 表字段 : br_config.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 应用Id
     * 表字段 : br_config.app_id
     * </pre>
     */
    private Integer appId;

    /**
     * <pre>
     * 键
     * 表字段 : br_config.config_key
     * </pre>
     */
    private String configKey;

    /**
     * <pre>
     * 值
     * 表字段 : br_config.config_value
     * </pre>
     */
    private String configValue;

    /**
     * <pre>
     * 预备值
     * 表字段 : br_config.pre_config_value
     * </pre>
     */
    private String preConfigValue;

    /**
     * <pre>
     * 版本号
     * 表字段 : br_config.key_version
     * </pre>
     */
    private String keyVersion;

    /**
     * <pre>
     * 所属文件
     * 表字段 : br_config.key_type
     * </pre>
     */
    private String keyType;

    /**
     * <pre>
     * 下发状态 0:未下发 1:已下发
     * 表字段 : br_config.key_status
     * </pre>
     */
    private Integer pushStatus;

    /**
     * <pre>
     * 键描述
     * 表字段 : br_config.key_des
     * </pre>
     */
    private String keyDes;

    /**
     * <pre>
     * 环境 0:开发环境 1：测试环境 2:预发布环境 3:生产环境
     * 表字段 : br_config.env_id
     * </pre>
     */
    private Integer envId;

    /**
     * 客户端同步状态
     */
    private List<MachineNodeDataVO> machineNodeDataList;
}
