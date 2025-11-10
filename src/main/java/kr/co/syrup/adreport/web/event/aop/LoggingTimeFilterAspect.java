package kr.co.syrup.adreport.web.event.aop;

import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import kr.co.syrup.adreport.web.event.entity.repository.CommonSettingsEntityRepository;
import kr.co.syrup.adreport.web.event.mybatis.mapper.ArEventMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 로깅 시간 체크 AOP
 */
@Slf4j
@Component
@Aspect
public class LoggingTimeFilterAspect {

    @Autowired
    private CommonSettingsEntityRepository commonSettingsEntityRepository;

    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.LoggingTimeFilter)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("filterPattern() && methodPointcut()")
    public Object loggingTimeFilterMethod(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String methodName  = methodSignature.getMethod().getName();

        Object result = pjp.proceed();

        stopWatch.stop();

        StringBuilder builder = new StringBuilder();
        builder.append("\n*********************************************************************************************************");
        builder.append("\n= LoggingTimeFilter");
        builder.append("\n= METHOD NAME : ").append(methodName);
        builder.append("\n= TOTAL RUN TIME : ").append(stopWatch.getTotalTimeMillis() / 1000).append("sec");
        builder.append("\n*********************************************************************************************************");

        log.info(builder.toString());

        //로깅 시간이 5초보다 크면 common_settings 테이블 저장
        if (stopWatch.getTotalTimeSeconds() > 5) {
            CommonSettingsEntity entity = new CommonSettingsEntity();
            entity.setSettingKey(methodName);
            entity.setValue(String.valueOf(stopWatch.getTotalTimeSeconds()));
            entity.setCommonSettingsDesc("메서드 로직 5초 초과 건");

            try {
                if (PredicateUtils.isNotEqualsStr(methodName, "extractionAttendCodeByExcelFile")) {
                    //commonSettingsEntityRepository.save(entity);
                }
            } catch (DuplicateKeyException de) {
                log.error(de.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }
}
