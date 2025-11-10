package kr.co.syrup.adreport.web.event.aop;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.response.api.SodarMemberResDto;
import kr.co.syrup.adreport.web.event.service.SodarApiService;
import kr.co.syrup.adreport.web.event.session.SodarMemberSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 소다 세션 체크 AOP
 */
@Slf4j
@Component
@Aspect
public class SodarMemberSessionAspect {

    @Autowired
    private SodarApiService sodarApiService;

    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.SetSodarMemberSession)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("filterPattern() && methodPointcut()")
    public Object sodarMemberSessionMethod(ProceedingJoinPoint pjp) throws Throwable {
        if (ProfileProperties.isProd()) {
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = req.getSession();

            try {
                //소다 로그인 확인
                SodarMemberResDto sodarMemberResDto = sodarApiService.checkSodarLoginMember(req.getCookies());

                if (PredicateUtils.isNull(sodarMemberResDto)) {
                    session.removeAttribute(SodarMemberSession.ATTR_NAME);
                    if (PredicateUtils.isNull(sodarMemberResDto)) {
                        throw new BaseException(ResultCodeEnum.SODAR_MEMBER_IS_NULL.getDesc(), ResultCodeEnum.SODAR_MEMBER_IS_NULL);
                    }
                } else {
                    SodarMemberSession.set(sodarMemberResDto);
                    session.setAttribute(SodarMemberSession.ATTR_NAME, sodarMemberResDto);
                    session.setMaxInactiveInterval(10 * 2500);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.info("errorCode >> " + ResultCodeEnum.SODAR_MEMBER_IS_NULL.getCode());
                throw new BaseException(ResultCodeEnum.SODAR_MEMBER_IS_NULL.getDesc(), ResultCodeEnum.SODAR_MEMBER_IS_NULL);
            }
        }
        return pjp.proceed();
    }
}
