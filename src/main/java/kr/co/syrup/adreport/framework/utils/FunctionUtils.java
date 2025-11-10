package kr.co.syrup.adreport.framework.utils;

import kr.co.syrup.adreport.web.event.define.StringDefine;

import java.util.function.BiFunction;

public class FunctionUtils {

    /**
     * 두개의 숫자를 > 0/0 형식으로 리턴
     * @param num1
     * @param num2
     * @return
     */
    public static String concatIntAddSlashString(int num1, int num2) {
        BiFunction<String, String, String> function = (s1, s2) -> {
            String s3 = s1 + "/" + s2;
            return s3;
        };
        return function.apply(String.valueOf(num1), String.valueOf(num2));
    }
}
