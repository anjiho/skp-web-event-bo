package kr.co.syrup.adreport.framework.utils;

/**
 * Created by ho on 2017. 2. 1..
 */
public class EnumUtils {
    public static <T extends Enum<T>> T enumValueOf(Class<T> enumType, String value) {
        T returnValue = null;

        for (final T element : enumType.getEnumConstants()) {
            if (element.toString().equals(value)) {
                returnValue = element;
                break;
            }
        }

        return returnValue;
    }
}
