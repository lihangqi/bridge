package com.bridge.console.interceptor;

import com.bridge.console.model.enums.PermissionEnum;
import com.bridge.console.service.account.ContextHolder;
import com.bridge.console.service.account.TokenHelper;
import com.bridge.console.annotation.NotCertification;
import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.Result;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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
public class TokenValidateInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private TokenHelper tokenHelper;

    @Autowired
    private ContextHolder contextHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            // 如果打了该注解是表示不需要进行登陆认证的的，直接放行
            NotCertification notCertification = ((HandlerMethod) handler).getMethodAnnotation(NotCertification.class);
            // 开启@NotCertification不需要token认证
            if (notCertification != null) {
                return super.preHandle(request, response, handler);
            }
            String token = request.getHeader("token");
            if (StringUtils.isEmpty(token)) {
                throw new BusinessCheckFailException(BaseErrorEnum.VALIDATE_ERROR);
            }
            // 验证token
            tokenHelper.verifyToken(token);
        } catch (Exception e) {
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(new Gson().toJson(this.writeResponse(e)));
            response.getWriter().flush();
            response.getWriter().close();
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
        result.setCode(PermissionEnum.VALIDATE_ERROR.getCode());
        result.setSuccess(Boolean.FALSE);
        result.setMessage(e.getMessage());
        return result;
    }
}
