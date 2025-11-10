package kr.co.syrup.adreport.framework.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * requestBody 특정값 암호화
 * @author 안지호
 * @사용법: 특정 property 에 aes256 암호화가 필요하면 컨트롤러에 @EncryptDataFilter("jsonProperty1,jsonProperty2",...) 형식으로 선언
 *        예외처리가 필요한 필드가 있으면 EncryptDataFilterAspect.class 클래서에서 예외처리 가능
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptDataFilter {
    String[] values() default {};
    String value() default "";
}
