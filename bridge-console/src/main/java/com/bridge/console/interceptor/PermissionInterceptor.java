package com.bridge.console.interceptor;

import com.bridge.console.annotation.AdminRequired;
import com.bridge.console.annotation.TeamLeaderRequired;
import com.bridge.console.model.enums.AccountRoleEnum;
import com.bridge.console.model.enums.PermissionEnum;
import com.bridge.console.service.account.ContextHolder;
import com.bridge.console.utils.EnumUtil;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.Result;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Jay
 * @version v1.0
 * @description 拦截器用于拦截所有的请求校验token
 * @date 2018-03-01 14:15
 */
@Slf4j
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private ContextHolder contextHolder;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            AdminRequired adminRequired = ((HandlerMethod) handler).getMethodAnnotation(AdminRequired.class);
            TeamLeaderRequired teamLeaderRequired = ((HandlerMethod) handler).getMethodAnnotation(TeamLeaderRequired.class);
            if (adminRequired == null && teamLeaderRequired == null) {
                return super.preHandle(request, response, handler);
            }
            Integer role = contextHolder.getSessionContext().getAccountRole();
            if (!EnumUtil.getKeys(AccountRoleEnum.class).contains(role)) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "账号角色异常");
            }
            // 只要不是管理员角色，都无法操作
            if (adminRequired != null && role != AccountRoleEnum.ROLE_ADMIN.getKey()) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "您不是管理员, 无权操作");
            }
            // 如果打了teamLeader的注解，那么管理员也可以访问，如果是打了管理员注解的，teamLeader是无法访问的
            if (teamLeaderRequired != null && role == AccountRoleEnum.ROLE_USER.getKey()) {
                throw new BusinessCheckFailException(BaseErrorEnum.BNS_CHK_ERROR.getCode(), "您不是团队Leader, 无权操作");
            }
        } catch (Exception e) {
            setResponse(response);
            response.getWriter().write(new Gson().toJson(this.writeResponse(e)));
            return Boolean.FALSE;
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清空业务session
        contextHolder.resetContext();
    }

    /**
     * token验证失败返回的response
     *
     * @param e
     */
    private Result writeResponse(Exception e) {
        Result result = new Result();
        result.setCode(PermissionEnum.PERMISSION_ERROR.getCode());
        result.setSuccess(Boolean.FALSE);
        result.setMessage(e.getMessage());
        return result;
    }


    /**
     * 设置response
     *
     * @param response
     */
    private void setResponse(HttpServletResponse response) {
        response.setContentType("application/json;charset=utf-8");
        response.setCharacterEncoding("utf-8");
    }
}
