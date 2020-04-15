package com.bridge.console.model.vo;

import lombok.Data;

import java.util.Date;


/**
 * @author Jay
 * @version v1.0
 * @description app定义
 * @date 2019-01-21 21:01
 */
@Data
public class AppDefVO {

    /**
     * <pre>
     *
     * 表字段 : br_app_def.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_app_def.gmt_create
     * </pre>
     */
    private Date gmtCreate;

    /**
     * <pre>
     * 系统编号
     * 表字段 : br_app_def.app_code
     * </pre>
     */
    private String appCode;

    /**
     * <pre>
     * 系统名称
     * 表字段 : br_app_def.app_name
     * </pre>
     */
    private String appName;

    /**
     * <pre>
     * 系统负责人
     * </pre>
     */
    private Integer appOwner;

    /**
     * <pre>
     * 所属团队
     * </pre>
     */
    private Integer teamId;


    /**
     * <pre>
     * 团队名称
     * </pre>
     */
    private String teamName;

    /**
     * <pre>
     * 负责人姓名
     * </pre>
     */
    private String ownerRealName;

    /**
     * <pre>
     * 系统描述
     * </pre>
     */
    private String appDes;
}