package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.aspect;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation.DS;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.context.DataSourceContextHolder;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.context.TransactionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.util.UUID;

/**
 * 类说明
 *
 * @author zhanghaoran
 */
@Aspect
public class LocalTransactionalAspect implements Ordered {

    /**
     * 声明AOP签名
     */
    @Pointcut("@annotation(com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation.DS)")
    public void pointcut() {
    }

    /**
     * 前置拦截
     * 反向查找代理方法类上的注解
     * DS annotation = AnnotatedElementUtils.findMergedAnnotation(signature.getMethod().getDeclaringClass(), DS.class);
     *
     * @param joinPoint 切面对象
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DS annotation = signature.getMethod().getAnnotation(DS.class);
        TransactionContext.bind(UUID.randomUUID().toString());
        DataSourceContextHolder.setDataSourceKey(annotation.value());
        try {
            return joinPoint.proceed();
        } finally {
            TransactionContext.remove();
            DataSourceContextHolder.clear();
        }
    }

    @Override
    public int getOrder() {
        // TODO Auto-generated method stub
        return 1;
    }
}
