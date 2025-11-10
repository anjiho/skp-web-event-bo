package kr.co.syrup.adreport.framework.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  @author 안지호
 *  @사용법 XSS 공격을 피하기 위한 컨트롤러에 @XssFilter 선언
 *  @생성일 2023. 1. 5
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XssFilter {
    String[] values() default {};
    String value() default "";
}
