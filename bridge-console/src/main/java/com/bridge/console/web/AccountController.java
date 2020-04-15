package com.bridge.console.web;

import com.bridge.console.annotation.TeamLeaderRequired;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.vo.EnumVO;
import com.bridge.console.model.vo.UserAccountBO;
import com.bridge.console.model.vo.UserAccountVO;
import com.bridge.console.model.vo.UserListQuery;
import com.bridge.console.service.account.ContextHolder;
import com.bridge.console.service.user.AccountService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.PageUtil;
import com.bridge.console.utils.result.PagingResult;
import com.bridge.console.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 用户相关
 * @date 2019-01-22 15:13
 */
@RestController
public class AccountController extends BaseController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ContextHolder contextHolder;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private TeamDefMapper teamDefMapper;

    /**
     * 修改密码
     *
     * @param oldPassword
     * @param newPassword
     * @return
     */
    @RequestMapping("/changePassword")
    @ResponseBody
    public Result<Boolean> changePassword(String oldPassword, String newPassword) {
        Integer accountId = contextHolder.getSessionContext().getId();
        return Result.wrapSuccessfulResult(accountService.changeUserPassword(oldPassword, newPassword, accountId));
    }


    /**
     * 添加用户，只有团队管理员和系统管理员才可以
     *
     * @param userAccountBo
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/addUser")
    @ResponseBody
    public Result<Boolean> addUser(UserAccountBO userAccountBo) {
        this.checkPermission(userAccountBo.getAccountRole(), userAccountBo.getTeamId());
        return Result.wrapSuccessfulResult(accountService.addUser(userAccountBo, contextHolder.getSessionContext().getId()));
    }


    /**
     * 编辑用户，只有团队管理员和系统管理员才可以
     *
     * @param userAccountBo
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/editUser")
    @ResponseBody
    public Result<Boolean> editUser(UserAccountBO userAccountBo) {
        checkPermission(userAccountBo.getAccountRole(), userAccountBo.getTeamId());
        return Result.wrapSuccessfulResult(accountService.editUser(userAccountBo, contextHolder.getSessionContext().getId()));
    }


    /**
     * 删除用户，只有团队管理员和系统管理员才可以
     *
     * @param accountId
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/deleteUser")
    @ResponseBody
    public Result<Boolean> deleteUser(Integer accountId) {
        checkPermission(accountId);
        return Result.wrapSuccessfulResult(accountService.deleteUser(accountId, contextHolder.getSessionContext().getId()));
    }


    /**
     * 用户列表分页查询接口，，只有团队管理员和系统管理员才可以
     *
     * @param userListQuery
     * @param pageable
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/queryUserPageList")
    @ResponseBody
    @SuppressWarnings("Duplicates")
    public PagingResult<UserAccountVO> queryUserList(UserListQuery userListQuery
            , @PageableDefault(page = 1, size = 20, sort = "gmt_create", direction = Sort.Direction.DESC) Pageable pageable) {
        if (isTeamLeader()) {
            if (userListQuery == null) {
                userListQuery = new UserListQuery();
            }
            userListQuery.setTeamId(getSessionContext().getTeamId());
        }
        int total = userAccountMapper.countUserPageList(userListQuery);
        if (total == 0) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        PageUtil.fillParam(userListQuery, pageable);
        List<UserAccountDO> userAccountDoList = userAccountMapper.queryUserPageList(userListQuery);
        if (CollectionUtils.isEmpty(userAccountDoList)) {
            return PagingResult.wrapSuccessfulResult(new ArrayList<>(), pageable, total);
        }
        List<UserAccountVO> userAccountVoList = BeanUtil.copyList(userAccountDoList, UserAccountVO.class);
        for (UserAccountVO userAccountVo : userAccountVoList) {
            userAccountVo.setTeamName(teamDefMapper.selectTeamNameById(userAccountVo.getTeamId()));
            userAccountVo.setAccountRoleStr(EnumUtil.getName(userAccountVo.getAccountRole(), AccountRoleEnum.class));
        }
        return PagingResult.wrapSuccessfulResult(userAccountVoList, pageable, total);
    }


    /**
     * 根据tag查询枚举,只有团队管理员和系统管理员才可以
     *
     * @param tag
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/queryEnumByTag")
    @ResponseBody
    public Result<List<EnumVO>> queryEnumByTag(Integer tag) {
        AccountRoleEnum accountRoleEnum = null;
        Integer teamId = null;
        if (isAdmin()) {
            accountRoleEnum = AccountRoleEnum.ROLE_ADMIN;
        }
        if (isTeamLeader() || isUser()) {
            accountRoleEnum = AccountRoleEnum.ROLE_TEAM_LEADER;
            teamId = getSessionContext().getTeamId();
        }
        return Result.wrapSuccessfulResult(accountService.queryEnumByTag(tag, teamId, accountRoleEnum));
    }


    /**
     * 启用或禁用，只有团队管理员和系统管理员才可以
     *
     * @param accountId
     * @param enabledState
     * @return
     */
    @TeamLeaderRequired
    @RequestMapping("/updateUserEnable")
    @ResponseBody
    public Result<Boolean> updateUserEnable(Integer accountId, Integer enabledState) {
        checkPermission(accountId);
        // 用户禁用、启用更新
        return Result.wrapSuccessfulResult(accountService.updateUserEnable(accountId, enabledState));
    }


    /**
     * 权限校验
     *
     * @param userAccountRole
     * @param teamId
     */
    private void checkPermission(Integer userAccountRole, Integer teamId) {
        if (userAccountRole == null || teamId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (userAccountRole == AccountRoleEnum.ROLE_ADMIN.getKey()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "你无权操作系统管理员账号");
        }
        if (isTeamLeader() || isUser()) {
            if (userAccountRole == AccountRoleEnum.ROLE_ADMIN.getKey()) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "你无权创建管理员角色的账号");
            }
            if (!teamId.equals(getSessionContext().getTeamId())) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "团队非法");
            }
        }
    }


    /**
     * 权限校验
     *
     * @param accountId
     */
    private void checkPermission(Integer accountId) {
        UserAccountDO userAccountDO = userAccountMapper.selectByPrimaryKey(accountId);
        this.checkPermission(userAccountDO.getAccountRole(), userAccountDO.getTeamId());
    }


}
