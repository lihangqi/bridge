package com.bridge.console.model.vo;

import lombok.Data;


/**
 * @author Jay
 * @version v1.0
 * @description team信息表
 * @date 2019-01-21 21:01
 */
@Data
public class TeamDefVO {
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