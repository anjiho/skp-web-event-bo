package kr.co.syrup.adreport.framework.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

public class CustomKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "_" + method.getName() + "_" +
                StringUtils.arrayToDelimitedString(params, "_");
    }

    /**
     * Generate a key based on the specified parameters.
     */
    public static Object generateKey(Object...params) {
        if (params.length == 0) {
            return CustomCacheKey.EMPTY;
        }
        if (params.length == 1) {
            Object param = params[0];
            if (param != null && !param.getClass().isArray()) {
                return param;
            }
        }
        return new CustomCacheKey(params);
    }
}
