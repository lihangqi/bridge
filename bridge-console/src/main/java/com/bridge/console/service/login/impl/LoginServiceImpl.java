package com.bridge.console.service.login.impl;

import com.bridge.console.model.constant.Constant;
import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.enums.EnabledStateEnum;
import com.bridge.console.service.account.SessionContext;
import com.bridge.console.service.account.TokenHelper;
import com.bridge.console.service.login.LoginService;
import com.bridge.console.utils.EncryptUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Jay
 * @version v1.0
 * @description 登录
 * @date 2019-01-22 11:07
 */
@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private TokenHelper tokenHelper;

    /**
     * 登录
     *
     * @param accountName
     * @param password
     * @return
     */
    @Override
    public SessionContext login(String accountName, String password) {
        if (StringUtils.isEmpty(accountName) || StringUtils.isEmpty(password)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号或密码不能为空");
        }
        UserAccountDO userAccountDO = userAccountMapper.selectByAccountName(accountName);
        // 账号存在性校验
        if (userAccountDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号不存在");
        }
        // 密码校验
        if (!EncryptUtil.getMd5Str(password).equals(userAccountDO.getPassword())) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "密码错误");
        }
        // 是否被禁用
        if (userAccountDO.getEnabledState() != EnabledStateEnum.ENABLED.getKey()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号已经被禁用");
        }
        // 重新生成token并更新
        String token = tokenHelper.operateToken(userAccountDO.getId());
        return tokenHelper.getSessionContextByToken(token);
    }


    /**
     * 退出登录
     *
     * @param accountId
     * @return
     */
    @Override
    public Boolean logout(Integer accountId) {
        if (userAccountMapper.updateTokenByAccountId(Constant.NULL, accountId) != 1) {
            throw new BusinessCheckFailException(BaseErrorEnum.UPDATE_ERROR);
        }
        return Boolean.TRUE;
    }
}
