package com.bridge.console.service.account;

import org.springframework.stereotype.Component;

/**
 * @author Jay
 * @version v1.0
 * @description 登陆的用户业务session信息
 * @date 2019-01-22 10:22
 */
@Component
public class ContextHolder {

    private ThreadLocal<SessionContext> contextThreadLocal = new ThreadLocal<>();

    /**
     * 根据userName初始化session信息
     *
     * @param sessionContext
     */
    public void initSessionContext(SessionContext sessionContext) {
        if (isSessionContextInitialized()) {
            throw new IllegalStateException("duplicated initialization of sessionContext,check if this method invoked somewhere");
        }
        contextThreadLocal.set(sessionContext);
    }

    /**
     * 获取业务session
     *
     * @return
     */
    public SessionContext getSessionContext() {
        return contextThreadLocal.get();
    }

    /**
     * 重置session
     */
    public void resetContext() {
        contextThreadLocal.remove();
    }

    //-----------------------------------------------private method-------------------------------------------------

    /**
     * 业务session是否被初始化
     *
     * @return
     */
    private boolean isSessionContextInitialized() {
        return contextThreadLocal.get() != null;
    }
}
