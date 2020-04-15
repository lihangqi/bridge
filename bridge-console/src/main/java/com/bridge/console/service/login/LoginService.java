package com.bridge.console.service.login;

import com.bridge.console.service.account.SessionContext;

/**
 * @author Jay
 * @version v1.0
 * @description 登录
 * @date 2019-01-22 11:05
 */
public interface LoginService {


    /**
     * 登录
     *
     * @param accountName
     * @param password
     * @return
     */
    SessionContext login(String accountName, String password);


    /**
     * 退出登录
     *
     * @param accountId
     * @return
     */
    Boolean logout(Integer accountId);
}
