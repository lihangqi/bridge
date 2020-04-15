package com.bridge.console.service.user.impl;

import com.bridge.console.model.dao.AppDefMapper;
import com.bridge.console.model.dao.TeamDefMapper;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.AppDefDO;
import com.bridge.console.model.entity.TeamDefDO;
import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.domain.Constants;
import com.bridge.enums.EnabledStateEnum;
import com.bridge.console.model.vo.EnumVO;
import com.bridge.console.model.vo.UserAccountBO;
import com.bridge.console.service.user.AccountService;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EncryptUtil;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.ex.BusinessProcessFailException;
import com.bridge.console.utils.result.BaseBizEnum;
import com.bridge.console.utils.result.BaseErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author Jay
 * @version v1.0
 * @description 账号相关
 * @date 2019-01-22 15:18
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {


    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private TeamDefMapper teamDefMapper;

    @Autowired
    private AppDefMapper appDefMapper;

    /**
     * 修改用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @param accountId
     * @return
     */
    @Override
    public Boolean changeUserPassword(String oldPassword, String newPassword, Integer accountId) {
        // 参数校验
        this.checkPassword(oldPassword, newPassword, accountId);
        // 判断旧密码是否正确
        UserAccountDO userAccountDO = userAccountMapper.selectByPrimaryKey(accountId);
        if (userAccountDO == null || StringUtils.isEmpty(userAccountDO.getPassword())) {
            log.info(this.getClass().getName() + "用户修改密码失败,该accountId不存在,--> {}", accountId);
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "用户修改密码失败,该账号不存在");
        }
        // 校验旧密码是否相等
        if (!userAccountDO.getPassword().equals(EncryptUtil.getMd5Str(oldPassword))) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "旧密码错误");
        }
        userAccountDO.setPassword(EncryptUtil.getMd5Str(newPassword));
        userAccountDO.setToken("NULL");
        // 修改密码
        if (userAccountMapper.updateByPrimaryKeySelective(userAccountDO) != 1) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "用户修改密码失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 根据tag查询枚举
     *
     * @param tag
     * @return
     */
    @Override
    @SuppressWarnings("Duplicates")
    public List<EnumVO> queryEnumByTag(Integer tag, Integer teamId, AccountRoleEnum accountRoleEnum) {
        if (tag == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "tag不能为空");
        }
        List<EnumVO> enumVoList = new ArrayList<>();
        // 角色枚举
        if (tag == BaseBizEnum.FIRST.getCode()) {
            // 普通用户查询返回空对象
            if (accountRoleEnum == AccountRoleEnum.ROLE_USER) {
                return enumVoList;
            }
            for (AccountRoleEnum roleEnum : AccountRoleEnum.values()) {
                EnumVO enumVO = new EnumVO();
                enumVO.setKey(roleEnum.getKey());
                enumVO.setValue(roleEnum.getName());
                // 团队管理员查询角色列表，则不显示系统管理员
                if (accountRoleEnum == AccountRoleEnum.ROLE_TEAM_LEADER && roleEnum == AccountRoleEnum.ROLE_ADMIN) {
                    continue;
                }
                enumVoList.add(enumVO);
            }
            return enumVoList;
        }
        // 团队的k/v
        if (tag == BaseBizEnum.SECOND.getCode()) {
            List<TeamDefDO> teamDefDoList = teamDefMapper.findAll(teamId);
            if (!CollectionUtils.isEmpty(teamDefDoList)) {
                teamDefDoList.forEach(item -> {
                    if (!item.getTeamName().equals(Constants.TEAM_NAME)) {
                        EnumVO enumVO = new EnumVO();
                        enumVO.setKey(item.getId());
                        enumVO.setValue(item.getTeamName());
                        enumVoList.add(enumVO);
                    }
                });
            }
            return enumVoList;
        }
        throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "参数异常");
    }

    /**
     * 添加用户
     *
     * @param userAccountBo
     * @return
     */
    @Override
    public Boolean addUser(UserAccountBO userAccountBo, Integer currentAccountId) {
        // 参数校验
        this.checkUserAccountBo(userAccountBo);
        // 校验账号是否存在
        UserAccountDO userAccountDO = userAccountMapper.selectByAccountName(userAccountBo.getAccountName());
        if (userAccountDO != null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号已经存在");
        }
        // 保存到数据库
        UserAccountDO addUserDO = new UserAccountDO();
        BeanUtil.copyProperties(userAccountBo, addUserDO);
        addUserDO.setPassword(EncryptUtil.getMd5Str(userAccountBo.getPassword()));
        addUserDO.setIsDeleted(BaseBizEnum.YN_N.getCode());
        addUserDO.setCreator(currentAccountId);
        addUserDO.setModifier(currentAccountId);
        addUserDO.setGmtCreate(new Date());
        addUserDO.setGmtModified(new Date());
        if (userAccountMapper.insertSelective(addUserDO) != 1) {
            throw new BusinessCheckFailException(BaseErrorEnum.SAVE_ERROR.getCode(), "新增用户失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 用户启用/禁用
     *
     * @param accountId
     * @param enabledState
     * @return
     */
    @Override
    public Boolean updateUserEnable(Integer accountId, Integer enabledState) {
        if (enabledState == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "enableState不能为空");
        }
        if (accountId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "id不能为空");
        }
        if (enabledState != EnabledStateEnum.ENABLED.getKey() && enabledState != EnabledStateEnum.NOT_ENABLED.getKey()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "enableState传入值不合法");
        }
        // 更新用户状态
        if (userAccountMapper.updateEnableById(accountId, enabledState) != 1) {
            throw new BusinessProcessFailException("更新用户状态失败", BaseErrorEnum.UPDATE_ERROR.getCode());
        }
        return Boolean.TRUE;
    }


    /**
     * 编辑用户
     *
     * @param userAccountBo
     * @param operateId
     * @return
     */
    @Override
    public Boolean editUser(UserAccountBO userAccountBo, Integer operateId) {
        // 参数校验
        this.checkUpdateUserParam(userAccountBo);
        // 判断记录是否存在
        UserAccountDO userAccountDO = userAccountMapper.selectByAccountName(userAccountBo.getAccountName());
        if (userAccountDO == null) {
            throw new BusinessProcessFailException("更新用户信息失败", BaseErrorEnum.UPDATE_ERROR.getCode());
        }
        // 团队发生变更
        if (userAccountBo.getTeamId() != null && !userAccountDO.getTeamId().equals(userAccountBo.getTeamId())) {
            // 判断是有负责的项目
            List<AppDefDO> appDefDoList = appDefMapper.selectByAppOwner(userAccountBo.getId());
            if (!CollectionUtils.isEmpty(appDefDoList)) {
                throw new BusinessProcessFailException("该用户还有负责的项目，请先变更项目负责人！", BaseErrorEnum.UPDATE_ERROR.getCode());
            }
        }
        // 更新信息
        UserAccountDO updateDO = new UserAccountDO();
        BeanUtil.copyProperties(userAccountBo, updateDO);
        updateDO.setModifier(operateId);
        updateDO.setToken("NULL");
        if (StringUtils.isEmpty(userAccountBo.getPassword())) {
            updateDO.setPassword(null);
        } else {
            updateDO.setPassword(EncryptUtil.getMd5Str(userAccountBo.getPassword()));
        }
        if (userAccountMapper.updateByPrimaryKeySelective(updateDO) != 1) {
            throw new BusinessProcessFailException("更新用户信息失败", BaseErrorEnum.UPDATE_ERROR.getCode());
        }
        return Boolean.TRUE;
    }

    /**
     * 删除账号
     *
     * @param accountId
     * @param operateId
     * @return
     */
    @Override
    public Boolean deleteUser(Integer accountId, Integer operateId) {
        if (accountId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "accountId不能为空");
        }
        // 如果该账号还有负责的系统，则需要先转移系统才能删除
        List<AppDefDO> appDefDoList = appDefMapper.selectByAppOwner(accountId);
        if (!CollectionUtils.isEmpty(appDefDoList)) {
            throw new BusinessProcessFailException("该用户还有负责的项目，请先变更项目负责人！", BaseErrorEnum.UPDATE_ERROR.getCode());
        }
        if (userAccountMapper.updateIsDeletedById(accountId, BaseBizEnum.YN_Y.getCode(), operateId) != 1) {
            throw new BusinessProcessFailException("删除用户信息失败", BaseErrorEnum.UPDATE_ERROR.getCode());
        }
        return Boolean.TRUE;
    }


    //-------------------------------------private method---------------------------------------------------------------


    /**
     * 参数校验
     *
     * @param userAccountBo
     */
    private void checkUpdateUserParam(UserAccountBO userAccountBo) {
        if (userAccountBo == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (StringUtils.isEmpty(userAccountBo.getAccountName())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号不能为空");
        }
    }


    /**
     * 参数校验
     *
     * @param userAccountBo
     */
    private void checkUserAccountBo(UserAccountBO userAccountBo) {
        if (userAccountBo == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "入参不能为空");
        }
        if (StringUtils.isEmpty(userAccountBo.getAccountName())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "用户名不能为空");
        }
        if (StringUtils.isEmpty(userAccountBo.getPassword())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "密码不能为空");
        }
        if (userAccountBo.getAccountRole() == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "角色不能为空");
        }
        if (!EnumUtil.getKeys(AccountRoleEnum.class).contains(userAccountBo.getAccountRole())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "角色不合法");
        }
    }


    /**
     * 参数校验
     *
     * @param oldPassword
     * @param newPassword
     * @param accountId
     */
    private void checkPassword(String oldPassword, String newPassword, Integer accountId) {
        if (StringUtils.isEmpty(oldPassword)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "旧密码不能为空");
        }
        if (StringUtils.isEmpty(newPassword)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "新密码不能为空");
        }
        if (oldPassword.equals(newPassword)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "新密码不能和旧密码相同");
        }
        if (accountId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "accountId不能为空");
        }
    }
}
