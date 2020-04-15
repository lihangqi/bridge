package com.bridge.console.web;

import com.bridge.console.annotation.NotCertification;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.vo.UserInfoVO;
import com.bridge.console.service.account.ContextHolder;
import com.bridge.console.service.account.SessionContext;
import com.bridge.console.service.login.LoginService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 登录
 * @date 2019-01-22 11:21
 */
@Controller
public class LoginController {


    @Autowired
    private LoginService loginService;

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private TeamDefMapper teamDefMapper;


    /**
     * 登录
     *
     * @param accountName
     * @param password
     * @return
     */
    @NotCertification
    @RequestMapping("/login")
    @ResponseBody
    public Result<UserInfoVO> login(String accountName, String password) {
        SessionContext sessionContext = loginService.login(accountName, password);
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(sessionContext, userInfoVO);
        userInfoVO.setTeamName(teamDefMapper.selectTeamNameById(sessionContext.getTeamId()));
        AccountRoleEnum accountRoleEnum = EnumUtil.getEnum(userInfoVO.getAccountRole(), AccountRoleEnum.class);
        if (accountRoleEnum == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号数据异常");
        }
        userInfoVO.setPermissionList(this.getPermissionList(accountRoleEnum));
        return Result.wrapSuccessfulResult(userInfoVO);
    }


    /**
     * 退出登录
     *
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public Result<Boolean> logout() {
        return Result.wrapSuccessfulResult(loginService.logout(contextHolder.getSessionContext().getId()));
    }


    //--------------------------------------private method-------------------------------------------


    /**
     * 取权限列表
     *
     * @param accountRoleEnum
     * @return
     */
    private List<String> getPermissionList(AccountRoleEnum accountRoleEnum) {
        List<String> permissionList = new ArrayList<>();
        // 工作台
        permissionList.add("/welcome");
        // zookeeper数据
        permissionList.add("/health_analysis");
        // 配置项管理
        permissionList.add("/config_manager");
        // 操作日志
        permissionList.add("/operateLog_manager");
        // 开发文档
        permissionList.add("/devDoc_manager");
        // bug反馈
        permissionList.add("/bug_feedBack");
        switch (accountRoleEnum) {
            case ROLE_ADMIN:
                // 系统管理
                permissionList.add("/system_manager");
                // 团队管理
                permissionList.add("/team_manager");
                // 账号管理
                permissionList.add("/account_info");
                break;
            case ROLE_USER:
                return permissionList;
            case ROLE_TEAM_LEADER:
                // 系统管理
                permissionList.add("/system_manager");
                // 账号管理
                permissionList.add("/account_info");
                break;
            default:
                break;

        }
        return permissionList;

    }


}
