package com.bridge.console.config.aop;

import com.bridge.console.utils.result.BaseResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author Jay
 * @version v1.0
 * @description 定义aop
 * @date 2018-11-27 17:31
 */
@Aspect
@Component
public class ResultInterceptor extends AbstractControllerResultInterceptor {


    /**
     * 切点定义,用于controller的切面
     */
    @Pointcut(value = "((@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
            "&& (execution(com.bridge.console.utils.result.Result *.*(..)))) " +
            "|| execution(com.bridge.console.utils.result.PagingResult *.*(..)))")
    @Override
    public void pointCutDefine() {

    }

    /**
     * 环绕通知
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "pointCutDefine()")
    @Override
    protected BaseResult intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.intercept(joinPoint);
    }
}
