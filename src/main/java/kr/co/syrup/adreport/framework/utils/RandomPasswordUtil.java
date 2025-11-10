package kr.co.syrup.adreport.framework.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RandomPasswordUtil {

    /**
     * 임시비밀번호 생성 6자리로 고정함
     * 영문대문자,영문소문자,숫자,특수문자(!, @, $, %, ^, &, *) 4종 각 2개씩 Random 생성하고 순서 섞어서 리턴함
     * @return
     */
    public static String generatePassword() {
        String[] charSetUpper = new String[] {
                "A", "B", "C", "D", "E", "F", "G", "H", /*"I",*/ "J", "K", "L", "M", "N", /*"O",*/ "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        };
        String[] charSetLower = new String[] {
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", /*"l",*/ "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
        };
        String[] charSetNumber = new String[] {
                /*"0", "1", */"2", "3", "4", "5", "6", "7", "8", "9"
        };
        String[] charSetSpecial = new String[] {
                //!, @, $, %, ^, &, *
                "!", "@", "$", "%", "^", "&", "*"
        };

        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        List<String> tempList=new ArrayList<>();

        // 영대문자
//        tempList.add(charSetUpper[sr.nextInt(charSetUpper.length)]);
//        tempList.add(charSetUpper[sr.nextInt(charSetUpper.length)]);
        // 영소문자
        tempList.add(charSetLower[sr.nextInt(charSetLower.length)]);
        tempList.add(charSetLower[sr.nextInt(charSetLower.length)]);
        // 숫자
        tempList.add(charSetNumber[sr.nextInt(charSetNumber.length)]);
        tempList.add(charSetNumber[sr.nextInt(charSetNumber.length)]);
        //특수문자
        tempList.add(charSetSpecial[sr.nextInt(charSetSpecial.length)]);
        tempList.add(charSetSpecial[sr.nextInt(charSetSpecial.length)]);

        // 섞기
        Collections.shuffle(tempList);
        return String.join("", tempList);
    }

    public static void main(String[] args) {
        System.out.println(generatePassword());
    }
}
