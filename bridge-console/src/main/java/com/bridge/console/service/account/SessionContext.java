package com.bridge.console.service.account;


import lombok.Data;

import java.io.Serializable;

/**
 * @author Jay
 * @version v1.0
 * @description 用户登陆的持久化session
 * @date 2019-01-22 10:22
 */
@Data
public class SessionContext implements Serializable {


    /**
     * id
     */
    private Integer id;

    /**
     * <pre>
     * 账号
     * 表字段 : br_user_account.account_name
     * </pre>
     */
    private String accountName;

    /**
     * <pre>
     * 角色 0: 管理员 1: 普通用户
     * 表字段 : br_user_account.account_role
     * </pre>
     */
    private Integer accountRole;

    /**
     * <pre>
     * 所属团队id
     * 表字段 : br_user_account.team_id
     * </pre>
     */
    private Integer teamId;

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
}
