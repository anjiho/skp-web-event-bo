package kr.co.syrup.adreport.framework.utils;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateUtils {

    /**
     * 문자열이 Y 인지 체크
     * @param checkValue
     * @return
     */
    public static boolean isEqualY(String checkValue) {
        Predicate<String> predicate = str -> "Y".equals(str);
        return predicate.test(checkValue.toUpperCase());
    }

    /**
     * 문자열이 N 인지 체크
     * @param checkValue
     * @return
     */
    public static boolean isEqualN(String checkValue) {
        Predicate<String> predicate = str -> "N".equals(str);
        return predicate.test(checkValue.toUpperCase());
    }

    /**
     * NULL 인지 체크
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj) {
        if (obj instanceof String) {
            if (StringUtils.isEmpty(obj.toString())) {
                return true;
            }
        } else {
            Predicate<Object> predicate = o -> o == null;
            return predicate.test(obj);
        }
        return false;
    }

    /**
     * NULL 이 아닌지 체크
     * @param obj
     * @return
     */
    public static boolean isNotNull(Object obj) {
        if (obj instanceof String) {
            if (StringUtils.isNotEmpty(obj.toString())) {
                return true;
            }
        } else {
            Predicate<Object> predicate = o -> o != null;
            return predicate.test(obj);
        }
        return false;
    }

    /**
     * 0보다 크면 true
     * @param i
     * @return
     */
    public static boolean isGreaterThanZero(int i) {
        Predicate<Integer> predicate = num -> num > 0;
        return predicate.test(i);
    }

    /**
     * 0 과 같으면 true
     * @param i
     * @return
     */
    public static boolean isEqualZero(int i) {
        Predicate<Integer> predicate = num -> num == 0;
        return predicate.test(i);
    }

    /**
     * standardNumber 와 equalNumber 같으면 true
     * @param standardNumber
     * @param equalNumber
     * @return
     */
    public static boolean isEqualNumber(int standardNumber, int equalNumber) {
        Predicate<Integer> predicate = num -> standardNumber == equalNumber;
        return predicate.test(equalNumber);
    }

    /**
     * num1 >= num2 이면 true 아니면 false
     * @param num1
     * @param num2
     * @return
     */
    public static boolean isGreaterThanEqualTo(int num1, int num2) {
        BiPredicate<Integer, Integer> biPredicate = (n1, n2) -> n1 >= n2;
        return biPredicate.test(num1, num2);
    }

    /**
     * standard 값이 start, end 구간 값 안에 들어가 있으면 true 아니면 false
     * @param start
     * @param standard
     * @param end
     * @return
     */
    public static boolean isInTwoSections(int start, int standard, int end) {
        Predicate<Integer> predicate1 = num -> start <= standard;
        Predicate<Integer> predicate2 = num -> end >= standard;

        return predicate1.and(predicate2).test(standard);

    }

    /**
     * num1 > num2 면 true
     * @param num1
     * @param num2
     * @return
     */
    public static boolean isGreaterThan(int num1, int num2) {
        BiPredicate<Integer, Integer> biPredicate = (n1, n2) -> n1 > n2;
        return biPredicate.test(num1, num2);
    }

    public static boolean isLowerThan(int num1, int num2) {
        BiPredicate<Integer, Integer> biPredicate = (n1, n2) -> n1 < n2;
        return biPredicate.test(num1, num2);
    }

    /**
     * 두개의 문자열이 같은지 확인
     * @param str1
     * @param str2
     * @return
     */
    public static boolean isEqualsStr(String str1, String str2) {
        Predicate<String> predicate = str -> str1.equals(str2);
        return predicate.test(str2);
    }

    /**
     * 두개의 문자열이 다른지 확인
     * @param str1
     * @param str2
     * @return
     */
    public static boolean isNotEqualsStr(String str1, String str2) {
        Predicate<String> predicate = str -> !str1.equals(str2);
        return predicate.test(str2);
    }

    public static boolean isStrLength(String str1, int len) {
        Predicate<Integer> predicate1 = str -> len == str1.length();
        return predicate1.test(str1.length());
    }

    /**
     *
     * @param list 중복이 있는 list
     * @param key 중복 여부를 판단하는 키값
     * @param <T> generic type
     * @return list
     */
    public static <T> List<T> deduplication(final List<T> list, Function<? super T, ?> key) {
        return list.stream().filter(deduplication(key)).collect(Collectors.toList());
    }

    private static <T> Predicate<T> deduplication(Function<? super T, ?>key) {
        final Set<Object> set = ConcurrentHashMap.newKeySet();
        return predicate -> set.add(key.apply(predicate));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static boolean isNullList(List<?> paramList) {
        if (CollectionUtils.isEmpty(paramList)) {
            return true;
        }
        if (!CollectionUtils.isEmpty(paramList)) {
            Predicate<List> predicate = list -> paramList.size() == 0 || CollectionUtils.isEmpty(paramList);
            return predicate.test(paramList);
        }
        return false;
    }

    public static boolean isNotNullList(List<?> paramList) {
        if (CollectionUtils.isEmpty(paramList)) {
            return false;
        }
        if (!CollectionUtils.isEmpty(paramList)) {
            Predicate<List> predicate = list -> paramList.size() > 0 || !CollectionUtils.isEmpty(paramList);
            return predicate.test(paramList);
        }
        return false;
    }

    public static void main(String[] args) {
        String a = null;
        String b = "aa";
        boolean bl = PredicateUtils.isNull(a);
        boolean bl1 = PredicateUtils.isNull(b);
        System.out.println(bl);
        System.out.println(bl1);
        if (bl && bl1) {
            System.out.println("111");
        }
    }
}
