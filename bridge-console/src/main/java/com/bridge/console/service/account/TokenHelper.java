package com.bridge.console.service.account;


import com.bridge.console.model.dao.UserAccountMapper;
import com.bridge.console.model.entity.UserAccountDO;
import com.bridge.enums.EnabledStateEnum;
import com.bridge.console.utils.BeanUtil;
import com.bridge.console.utils.EncryptUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Jay
 * @version v1.0
 * @description token相关操作
 * @date 2019-01-22 10:22
 */
@Component
public class TokenHelper {


    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private ContextHolder contextHolder;

    /**
     * token认证
     *
     * @param token
     */
    public void verifyToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "token不能为空");
        }
        SessionContext sessionContext = getSessionContextByToken(token);
        // 是否被禁用
        if (sessionContext.getEnabledState() != EnabledStateEnum.ENABLED.getKey()) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号已被禁用");
        }
        // 初始化session
        contextHolder.initSessionContext(sessionContext);
    }


    /**
     * 初始化一个token,更新到指定的账号下
     *
     * @param accountId
     * @return
     */
    public String operateToken(Integer accountId) {
        if (accountId == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "accountId不能为空");
        }
        String str = accountId.toString().concat(String.valueOf(System.currentTimeMillis()));
        String token = EncryptUtil.getToken(str);
        // 更新token到数据库
        if (userAccountMapper.updateTokenByAccountId(token, accountId) != 1) {
            throw new BusinessCheckFailException(BaseErrorEnum.UPDATE_ERROR);
        }
        return token;
    }


    /**
     * 根据token取SessionContext
     *
     * @param token
     * @return
     */
    public SessionContext getSessionContextByToken(String token) {
        UserAccountDO userAccountDO = userAccountMapper.selectByToken(token);
        if (userAccountDO == null) {
            throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "token认证失败");
        }
        SessionContext sessionContext = new SessionContext();
        BeanUtil.copyProperties(userAccountDO, sessionContext);
        return sessionContext;
    }


}
