package kr.co.syrup.adreport.framework.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 1) ApiResultObject 의 TraceNo response 가 필요한 컨트롤러에 @TraceNo 어노테이션을 선언해주면 된다.
 * 2) Request 시 traceNo가 null or 빈 공백값이면 traceNo 생성후 return.
 * 3) Request 시 traceNo가 있으면 traceNo request traceNo값 바로 return.
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TraceNoFilter {
}
