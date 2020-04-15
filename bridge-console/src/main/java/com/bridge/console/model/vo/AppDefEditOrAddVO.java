package com.bridge.console.model.vo;

import lombok.Data;


/**
 * @author Jay
 * @version v1.0
 * @description app定义表
 * @date 2019-01-21 21:01
 */
@Data
public class AppDefEditOrAddVO {

    /**
     * <pre>
     *
     * 表字段 : br_app_def.id
     * </pre>
     */
    private Integer id;


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
     * 表字段 : br_app_def.app_owner
     * </pre>
     */
    private Integer appOwner;

    /**
     * <pre>
     * 所属团队
     * 表字段 : br_app_def.team_id
     * </pre>
     */
    private Integer teamId;

    /**
     * <pre>
     * 系统描述
     * 表字段 : br_app_def.app_des
     * </pre>
     */
    private String appDes;
}