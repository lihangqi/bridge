package com.bridge.console.service.user;

import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.vo.EnumVO;
import com.bridge.console.model.vo.UserAccountBO;

import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description 用户相关
 * @date 2019-01-22 15:17
 */
public interface AccountService {


    /**
     * 修改用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @param accountId
     * @return
     */
    Boolean changeUserPassword(String oldPassword, String newPassword, Integer accountId);


    /**
     * 添加用户
     *
     * @param userAccountBo
     * @param currentAccountId
     * @return
     */
    Boolean addUser(UserAccountBO userAccountBo, Integer currentAccountId);


    /**
     * 根据tag查询枚举
     *
     * @param tag
     * @param teamId
     * @param accountRoleEnum
     * @return
     */
    List<EnumVO> queryEnumByTag(Integer tag, Integer teamId, AccountRoleEnum accountRoleEnum);


    /**
     * 用户启用/禁用
     *
     * @param accountId
     * @param enabledState
     * @return
     */
    Boolean updateUserEnable(Integer accountId, Integer enabledState);


    /**
     * 编辑用户
     *
     * @param userAccountBo
     * @param operateId
     * @return
     */
    Boolean editUser(UserAccountBO userAccountBo, Integer operateId);


    /**
     * 删除账号
     *
     * @param accountId
     * @param operateId
     * @return
     */
    Boolean deleteUser(Integer accountId, Integer operateId);
}
