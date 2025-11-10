package kr.co.syrup.adreport.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {

    public static final String pattern1 = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{6,20}$"; // 영문, 숫자, 특수문자
    public static final String pattern2 = "^[A-Za-z[0-9]]{10,20}$"; // 영문, 숫자
    public static final String pattern3 = "^[[0-9]$@$!%*#?&]{10,20}$"; //영문,특수문자
    public static final String specialCharactersPattern = "[$@$!%*#?&]"; // 특수문자, 숫자
    public static final String pattern5 = "(\\w)\\1\\1\\1"; // 같은 문자, 숫자

    /**
     * 비밀번호 체크 정규식 (영문 + 숫자 + 문자 6자리 이상 20자리 이하)
     * @param pwd
     * @return
     */
    public static boolean isPwdRegularExpression(String pwd) {
        Matcher match;
        match = Pattern.compile(pattern1).matcher(pwd);
        return match.find();
    }

    /**
     * 특수문자 패턴 체크
     * @param pwd
     * @return
     */
    public static boolean isSpecialCharactersPattern(String pwd) {
        Matcher match;
        match = Pattern.compile(specialCharactersPattern).matcher(pwd);
        return match.find();
    }

    /**
     * 핸드폰번호 패턴 체크
     * @param phoneNumber
     * @return
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Define the pattern for a valid phone number
        String pattern = "^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$";

        // Create a Pattern object
        Pattern regexPattern = Pattern.compile(pattern);

        // Create a Matcher object
        Matcher matcher = regexPattern.matcher(phoneNumber);

        // Check if the phone number matches the pattern
        return matcher.matches();
    }

    public static void main(String[] args) {
        String phoneNumber1 = "010-1234-5678";
        String phoneNumber2 = "02-987-6543";
        String phoneNumber3 = "011-8765-4321";
        String phoneNumber4 = "01012345678";
        String phoneNumber5 = "01112345678";

        System.out.println("Phone number 1 is valid: " + isValidPhoneNumber(phoneNumber1));
        System.out.println("Phone number 2 is valid: " + isValidPhoneNumber(phoneNumber2));
        System.out.println("Phone number 3 is valid: " + isValidPhoneNumber(phoneNumber3));
        System.out.println("Phone number 4 is valid: " + isValidPhoneNumber(phoneNumber4));
        System.out.println("Phone number 5 is valid: " + isValidPhoneNumber(phoneNumber5));

        //String str = "{'phoneNumber':'01062585228', 'name':'안지호'}";
        //StringTools.containsIgnoreCase(str, is)
    }

}
