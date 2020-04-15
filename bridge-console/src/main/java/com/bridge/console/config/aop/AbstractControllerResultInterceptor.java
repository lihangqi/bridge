package com.bridge.console.config.aop;


import com.bridge.console.utils.ex.BusinessCheckFailException;
import com.bridge.console.utils.ex.BusinessProcessFailException;
import com.bridge.console.utils.result.BaseErrorEnum;
import com.bridge.console.utils.result.BaseResult;
import com.bridge.console.utils.result.ServiceError;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.objenesis.ObjenesisStd;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * @version v1.0
 * @description controller的aop, 用于输出日志和规范返回类型
 * <p>
 *     该抽象方法目的是为了不需要在spring中定义xml配置，通过子类继续即可完成拦截的功能
 * <p>
 * @date 2018-11-27 16:42
 */
@Slf4j
public abstract class AbstractControllerResultInterceptor {

    private ObjenesisStd generator = new ObjenesisStd();

    /**
     * 此方法用于定义切点，子类实现了该类后需要重写该方法，并在该方法上打上注解定义需要的表达式
     * <p>
     *
     * @Pointcut(value = "(@annotation(org.springframework.web.bind.annotation.RequestMapping) " +
     * "&& execution(com.cloudm.framework.common.web.result.Result *.*(..))) " +
     * "|| (@annotation(org.springframework.web.bind.annotation.RequestMapping))")
     * </>
     */
    public abstract void pointCutDefine();


    /**
     * 该方法为环绕通知的核心方法，子类需要重写该方法，并在方法上打上切面的注解  @Around
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    protected BaseResult intercept(ProceedingJoinPoint joinPoint) throws Throwable {
        BaseResult result;
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            result = (BaseResult) joinPoint.proceed();
            watch.stop();
            if (log.isInfoEnabled()) {
                String info = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
                log.info("The Method==>{}, execute Time==>{}ms", info, watch.getTime());
            }
            return result;
        } catch (BusinessProcessFailException e) {
            watch.stop();
            result = exceptionProcessor(joinPoint, e, e.getErrorCode(), e.getMessage(), BaseErrorEnum.BNS_PRS_ERROR);
        } catch (BusinessCheckFailException e) {
            watch.stop();
            result = exceptionProcessor(joinPoint, e, e.getErrorCode(), e.getMessage(), BaseErrorEnum.BNS_CHK_ERROR);
        } catch (Exception e) {
            watch.stop();
            result = exceptionProcessor(joinPoint, e, BaseErrorEnum.UNKNOWN_ERROR);
        }
        return result;
    }


    /**
     * 异常处理 记录日志并返回result对象
     *
     * @param joinPoint
     * @param serviceError {@link BaseErrorEnum}
     * @param e
     * @return
     */
    private BaseResult exceptionProcessor(ProceedingJoinPoint joinPoint, Throwable e, Integer errorCode, String message, ServiceError serviceError) {
        Object[] args = joinPoint.getArgs();
        List<Object> list = new ArrayList<>();

        for (Object arg : args) {
            if (arg instanceof BindingResult || arg instanceof ServletResponse || arg instanceof ServletRequest || arg instanceof Servlet) {
                continue;
            }
            list.add(arg);
        }
        String methodName = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()";
        if (!list.isEmpty()) {
            log.error("服务[method=" + methodName + "] params={}" + new Gson().toJson(list) + "异常：", e);
        } else {
            log.error("服务[method=" + methodName + "] params={}" + new Gson().toJson(null) + "异常：", e);
        }
        BaseResult result = getBaseResult(joinPoint);
        result.setCode(errorCode != null ? errorCode : serviceError.getCode());
        result.setMessage(!StringUtils.isEmpty(message) ? message : serviceError.getMessage());
        result.setSuccess(false);
        return result;
    }


    /**
     * 返回result对象
     *
     * @param joinPoint
     * @param e
     * @param serviceError
     * @return
     */
    private BaseResult exceptionProcessor(ProceedingJoinPoint joinPoint, Throwable e, ServiceError serviceError) {
        return exceptionProcessor(joinPoint, e, null, null, serviceError);
    }


    /**
     * 反射获取返回值对象
     *
     * @param joinPoint 目标方法
     * @return
     */

    private BaseResult getBaseResult(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("类型转换失败");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = joinPoint.getTarget();
        Method currentMethod;
        try {
            currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        } catch (NoSuchMethodException e) {
           throw new BusinessCheckFailException(BaseErrorEnum.SYS_ERROR.getCode(),"反射获取返回值对象失败");
        }
        Class<?> returnType = currentMethod.getReturnType();
        return (BaseResult) generator.newInstance(returnType);
    }

}
