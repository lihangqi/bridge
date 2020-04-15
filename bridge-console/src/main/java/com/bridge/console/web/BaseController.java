package com.bridge.console.web;

import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.service.account.ContextHolder;
import com.bridge.console.service.account.SessionContext;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Jay
 * @version v1.0
 * @description 基类封装登录人角色相关信息
 * @date 2019-01-24 17:12
 */
@Component
public class BaseController {


    @Autowired
    private ContextHolder contextHolder;


    /**
     * 是否为管理员
     *
     * @return
     */
    public boolean isAdmin() {
        return judgeAccountRole(JudgeTypeEnum.ROLE_ADMIN_TYPE);
    }


    /**
     * 是否为普通用户
     *
     * @return
     */
    public boolean isUser() {
        return judgeAccountRole(JudgeTypeEnum.ROLE_USER_TYPE);
    }

    /**
     * 是否为团队leader
     *
     * @return
     */
    public boolean isTeamLeader() {
        return judgeAccountRole(JudgeTypeEnum.ROLE_TEAM_LEADER_TYPE);
    }

    /**
     * 获取登录人身份
     *
     * @return
     */
    public SessionContext getSessionContext() {
        SessionContext sessionContext = contextHolder.getSessionContext();
        if (sessionContext == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "sessionContext为空");
        }
        return sessionContext;
    }


    /**
     * 获取用户id
     *
     * @return
     */
    public Integer getUserId() {
        Integer userId = getSessionContext().getId();
        if (userId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "userId为空");
        }
        return userId;
    }

    /**
     * 获取用户角色 0: 管理员 1: 普通用户
     *
     * @return
     */
    public Integer getAccounntRole() {
        Integer accountRole = getSessionContext().getAccountRole();
        if (accountRole == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "accountRole为空");
        }
        return accountRole;
    }


    /**
     * 获取teamId
     *
     * @return
     */
    public Integer getTeamId() {
        Integer teamId = getSessionContext().getTeamId();
        if (teamId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "teamId为空");
        }
        return teamId;
    }


    //-----------------------------------------------private method----------------------------------------------------

    /**
     * 当前登录角色是否为admin
     *
     * @return
     */
    private boolean judgeAccountRole(JudgeTypeEnum judgeTypeEnum) {
        Integer accountRole = getSessionContext().getAccountRole();
        if (!EnumUtil.getKeys(AccountRoleEnum.class).contains(accountRole)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "角色非法");
        }
        switch (judgeTypeEnum) {

            case ROLE_ADMIN_TYPE:
                return accountRole == AccountRoleEnum.ROLE_ADMIN.getKey();

            case ROLE_USER_TYPE:
                return accountRole == AccountRoleEnum.ROLE_USER.getKey();

            case ROLE_TEAM_LEADER_TYPE:
                return accountRole == AccountRoleEnum.ROLE_TEAM_LEADER.getKey();

            default:
                break;
        }
        throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "参数异常");
    }


    /**
     * 判断类型枚举
     */
    private enum JudgeTypeEnum {
        // 判断管理员类型
        ROLE_ADMIN_TYPE,
        // 用户类型
        ROLE_USER_TYPE,
        // 团队管理员类型
        ROLE_TEAM_LEADER_TYPE
    }

}
