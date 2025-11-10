package kr.co.syrup.adreport.web.event.aop;

import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.common.annotation.ThreadSleep;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

/**
 * 쓰레드 AOP
 */
@Slf4j
@Order(1)
@Component
@Aspect
public class ThreadSleepAspect {

    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.ThreadSleep)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("filterPattern() && methodPointcut()")
    public Object threadSleepAspectMethod(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        ThreadSleep filter = methodSignature.getMethod().getAnnotation(ThreadSleep.class);
        long sleep = filter.value();
        log.info("sleep >> " + sleep);
        try {
            Thread.sleep(sleep);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pjp.proceed();
    }
}
