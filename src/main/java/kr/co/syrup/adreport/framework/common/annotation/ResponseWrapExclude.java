package kr.co.syrup.adreport.framework.common.annotation;

import java.lang.annotation.*;

/**
 * Created by jino on 2017. 1. 25.
 * ResponseBody의 결과 공통적용 제외 Annotation
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseWrapExclude {
}
