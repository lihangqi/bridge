package com.bridge.console.model.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * @author Jay
 * @version v1.0
 * @description 用户信息表
 * @date 2019-01-21 21:01
 */
@Data
public class UserAccountDO implements Serializable {

    /**
     * <pre>
     *
     * 表字段 : br_user_account.id
     * </pre>
     */
    private Integer id;

    /**
     * <pre>
     * 创建人
     * 表字段 : br_user_account.creator
     * </pre>
     */
    private Integer creator;

    /**
     * <pre>
     * 创建时间
     * 表字段 : br_user_account.gmt_create
     * </pre>
     */
    private Date gmtCreate;

    /**
     * <pre>
     * 修改人
     * 表字段 : br_user_account.modifier
     * </pre>
     */
    private Integer modifier;

    /**
     * <pre>
     * 修改时间
     * 表字段 : br_user_account.gmt_modified
     * </pre>
     */
    private Date gmtModified;

    /**
     * <pre>
     * 0:未删除，1：已删除
     * 表字段 : br_user_account.is_deleted
     * </pre>
     */
    private Integer isDeleted;

    /**
     * <pre>
     * 账号
     * 表字段 : br_user_account.account_name
     * </pre>
     */
    private String accountName;

    /**
     * <pre>
     * 角色 0: 管理员 1: 普通用户 2:团队leader
     * 表字段 : br_user_account.account_role
     * </pre>
     */
    private Integer accountRole;

    /**
     * <pre>
     * 团队Id
     * 表字段 : br_user_account.team_id
     * </pre>
     */
    private Integer teamId;

    /**
     * <pre>
     * 密码
     * 表字段 : br_user_account.password
     * </pre>
     */
    private String password;

    /**
     * <pre>
     * 用户token
     * 表字段 : br_user_account.token
     * </pre>
     */
    private String token;

    /**
     * <pre>
     * 姓名
     * 表字段 : br_user_account.real_name
     * </pre>
     */
    private String realName;

    /**
     * <pre>
     * 手机号
     * 表字段 : br_user_account.account_mobile
     * </pre>
     */
    private String accountMobile;

    /**
     * <pre>
     * 邮箱
     * 表字段 : br_user_account.email
     * </pre>
     */
    private String email;

    /**
     * <pre>
     * 启用状态 1:是 0:否
     * 表字段 : br_user_account.enabled_state
     * </pre>
     */
    private Integer enabledState;


    /**
     * <pre>
     * 成功下发的次数
     * 表字段 : br_user_account.push_count
     * </pre>
     */
    private Integer pushCount;

}