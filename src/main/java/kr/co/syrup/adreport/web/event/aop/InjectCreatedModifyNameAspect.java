package kr.co.syrup.adreport.web.event.aop;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 세션기준 입력자와 수정자 정보 주입하기 AOP
 */
@Slf4j
@Component
@Aspect
public class InjectCreatedModifyNameAspect {

    @Pointcut("@within(org.springframework.stereotype.Service)")
    public void service() {}

    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.InjectCreatedModifyName)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("filterPattern() && methodPointcut()")
    public Object InjectCreatedModifyNameMethod(ProceedingJoinPoint pjp) throws Throwable {
        Object[] signatureArgs = pjp.getArgs();
        for (Object signatureArg: signatureArgs) {
            if (signatureArg.getClass().getTypeName().contains("kr.co.syrup.adreport")) {
                Class<?> clazz = signatureArg.getClass();

                Field field = clazz.getDeclaredField("createdBy");
                if (PredicateUtils.isNotNull(field)) {
                    field.setAccessible(true);
                    field.set(signatureArg, PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자");
                    field.setAccessible(false);
                }

                Field field2 = clazz.getDeclaredField("lastModifiedBy");
                if (PredicateUtils.isNotNull(field2)) {
                    field2.setAccessible(true);
                    field2.set(signatureArg, PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자");
                    field2.setAccessible(false);
                }
            }
            if (signatureArg instanceof List) {
                for (Object object : (List) signatureArg) {
                    if (object.getClass().getTypeName().contains("kr.co.syrup.adreport")) {
                        Class<?> clazz = object.getClass();

                        Field field = clazz.getDeclaredField("createdBy");
                        if (PredicateUtils.isNotNull(field)) {
                            field.setAccessible(true);
                            field.set(object, PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자");
                            field.setAccessible(false);
                        }

                        Field field2 = clazz.getDeclaredField("lastModifiedBy");
                        if (PredicateUtils.isNotNull(field2)) {
                            field2.setAccessible(true);
                            field2.set(object, PredicateUtils.isNotNull(SodarMemberSession.get()) ? SodarMemberSession.get().getName() : "개발자");
                            field2.setAccessible(false);
                        }
                    }
                }
            }
        }
        return pjp.proceed();
    }


}
