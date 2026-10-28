package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.aspect;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation.MDS;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.context.DataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * mapper aop
 *
 * @author zhanghaoran
 */
@Aspect
public class MapperDatasourceAspect {

    /**
     * 声明AOP签名
     */
    @Pointcut("@within(com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation.MDS)")
    public void pointcut() {
    }

    /**
     * 前置拦截
     *
     * @param joinPoint 切面对象
     */
    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            MDS annotation = AnnotatedElementUtils.findMergedAnnotation(signature.getMethod().getDeclaringClass(), MDS.class);
            if (annotation != null) {
                DataSourceContextHolder.setDataSourceKey(annotation.value());
            }
        }

    }

    /**
     * 前置拦截
     *
     * @param joinPoint 切面对象
     */
    @After("pointcut()")
    public void after(JoinPoint joinPoint) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            DataSourceContextHolder.clear();
        }
    }
}
