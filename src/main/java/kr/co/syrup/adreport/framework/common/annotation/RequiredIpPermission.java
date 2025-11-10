package kr.co.syrup.adreport.framework.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정아이피에 대해 접근 권한을 주어야 할 경우 사용
 * 접근가능 아이피는 yml에 기술 (required.permission.permitList.ips)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredIpPermission {
}
