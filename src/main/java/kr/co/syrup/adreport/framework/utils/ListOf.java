package kr.co.syrup.adreport.framework.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * <PRE>
 * 1. Comment : GSON fromJson함수를 이용한 Class를 파라미터사용 Object
 * 2. 작성자 : 안지호
 * 3. 작성일 : 2018. 06 .28
 * </PRE>
 * @param <T>
 */
public class ListOf<T> implements ParameterizedType {

    private final Class<T> type;

    public ListOf(Class<T> type) {
        this.type = type;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] { type };
    }

    @Override
    public Type getRawType() {
        return List.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
