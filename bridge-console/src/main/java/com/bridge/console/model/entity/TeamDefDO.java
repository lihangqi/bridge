package com.bridge.console.model.entity;

import java.util.Date;
import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description team信息表
 * @date 2019-01-21 21:01
 */
@Data
public class TeamDefDO {
    /**
     * <pre>
     * 
     * 表字段 : br_team_def.id
     * </pre>
     * 
     */
    private Integer id;

    /**
     * <pre>
     * 创建人
     * 表字段 : br_team_def.creator
     * </pre>
     * 
     */
    private Integer creator;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_team_def.gmt_create
     * </pre>
     * 
     */
    private Date gmtCreate;

    /**
     * <pre>
     * 修改人
     * 表字段 : br_team_def.modifier
     * </pre>
     * 
     */
    private Integer modifier;

    /**
     * <pre>
     * 修改时间
     * 表字段 : br_team_def.gmt_modified
     * </pre>
     * 
     */
    private Date gmtModified;

    /**
     * <pre>
     * 0:未删除，1：已删除
     * 表字段 : br_team_def.is_deleted
     * </pre>
     * 
     */
    private Integer isDeleted;

    /**
     * <pre>
     * 团队名称
     * 表字段 : br_team_def.team_name
     * </pre>
     * 
     */
    private String teamName;

    /**
     * <pre>
     * 团队描述
     * 表字段 : br_team_def.team_des
     * </pre>
     * 
     */
    private String teamDes;
}