package kr.co.syrup.adreport.web.event.aop;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.common.annotation.XssFilter;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XssFilter AOP
 */
@Slf4j
@Component
@Aspect
public class XssFilterAspect {

    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.XssFilter)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("filterPattern() && methodPointcut()")
    public Object afterControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        XssFilter filter = methodSignature.getMethod().getAnnotation(XssFilter.class);

        Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();
        int index = -1;

        for (Annotation[] annotations : annotationMatrix) {
            index++;
            for (Annotation annotation : annotations) {

                if ((annotation instanceof RequestBody) || (annotation instanceof RequestPart)) {

                    Object requestBody = pjp.getArgs()[index];

                    if ((PredicateUtils.isNotNull(requestBody))) {

                        //공백값이 아닐때만 암호화
                        com.nhncorp.lucy.security.xss.XssFilter xssFilter = com.nhncorp.lucy.security.xss.XssFilter.getInstance("lucy-xss-superset.xml");
                        String filteredValue = xssFilter.doFilter(requestBody.toString());
                        Pattern p = Pattern.compile("<!-- Not Allowed Tag Filtered -->");
                        Matcher matcher = p.matcher(filteredValue);

                        //특수문자에 걸리면 에러처리
                        if (matcher.find()) {
                            log.error("XSS ERROR!");
                            throw new BaseException(ResultCodeEnum.CUSTOM_ERROR_XSS.getDesc(), ResultCodeEnum.CUSTOM_ERROR_XSS);
                        }
                    }
                }
            }
        }
        return pjp.proceed();
    }
}
