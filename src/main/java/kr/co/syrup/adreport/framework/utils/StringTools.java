package kr.co.syrup.adreport.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.syrup.adreport.survey.go.define.AgeTypeDefine;
import kr.co.syrup.adreport.web.event.define.KasApiDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.EventWinningReqDto;
import kr.co.syrup.adreport.web.event.dto.request.ProximityApiReqDto;
import kr.co.syrup.adreport.web.event.dto.response.api.OcbPointApiResDto;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author 
 *
 */
@Slf4j
public class StringTools {

    public static final String NEW_LINE = System.getProperty("line.separator", "\n");

    public static String null2blank(String src) {
        if(src == null) return "";
        return src;
    }

    public static String null2dash(String src) {
        if(src == null || src.equals("")) return "-";
        return src;
    }

    /** 주민번호 중간에 dash 넣기  */
    public static String makeJuminNO(String str) {

        String temp = null;
        int len = (str.trim()).length();

        if (len != 13)
            return str;
        temp = str.substring(0,6) +"-"+ str.substring(6,13) ;

        return temp;
    }

    /** number formating, 숫자에 ','를 첨가한다. */
    public static String addComma(String str) {

        String temp = null;

        if (str == null)
            temp = "0";
        else {
            double change = Double.valueOf(str.trim()).doubleValue();
            DecimalFormat decimal = new DecimalFormat("###,###,###,###");
            temp = decimal.format(change);
        }

        return temp;
    }

    /** 환율 number formating, 숫자에 ','를 첨가한다. */
    public static String eRateFormat(String str) {

        String temp = null;

        if (str == null)
            temp = "0";
        else {
            double change = Double.valueOf(str.trim()).doubleValue();
            DecimalFormat decimal = new DecimalFormat("###,###,###.##");
            temp = decimal.format(change);
        }

        return temp;
    }


    /**  어떤 char(1문자)를 문자열에서 삭제한다. */
    public static String delChar(String s, char delChar) {
        if(s == null) return "";
        StringBuffer sb = new StringBuffer();
        for(int i=0 ; i < s.length() ; i++) {
            char c = s.charAt(i);
            if(c == delChar) continue;
            sb.append(c);
        }
        return sb.toString();
    }


    /**  ','를 삭제한다. */
    public static String delComma(String str)   {
        return delChar(str, ',');
    }

    /**  '-'를 삭제한다. */
    public static String delDash(String s) {
        return delChar(s, '-');
    }

    /**  문자열에서 '.'를 삭제한다. (예 : yyyy.mm.dd 에서 ) */
    public static String delDot(String str) {
        return delChar(str, '.');
    }

    /**  문자열에서 ' '(space)를 삭제한다.  (추가 2002.06.03)*/
    public static String delSpace(String s) {
        return delChar(s, ' ');
    }


    /** 년월일 사이에 '.'를 첨가한다. */
    public static String date(String str) {

        String temp = null;
        if (str == null || (str.trim()).length()==0) return "";
        int len = str.length();

        if (len != 8)
            return str;
        if ((str.equals("00000000"))||(str.equals("    0")))
            return "";
        temp = str.substring(0,4) + "." + str.substring(4,6)
                + "." + str.substring(6,8);

        return  temp;
    }
    /** 년월일 사이에 '-'를 첨가한다. */
    public static String dateDash(String str) {
        if (str == null || (str.trim()).length()==0) return "";
        String temp = null;
        int len = str.length();

        if (len != 8)
            return str;
        if ((str.equals("00000000"))||(str.equals("    0")))
            return "";
        temp = str.substring(0,4) + "-" + str.substring(4,6)
                + "-" + str.substring(6,8);

        return  temp;
    }

    /** 년월일 한글로 표시한다 */
    /* by hjun 2000.12.06 */
    public static String dateHan(String str) {

        String temp = null;
        int len = str.length();

        if (len != 8)
            return str;
        if ((str.equals("00000000"))||(str.equals("    0")))
            return "";
        temp = str.substring(0,4) + "년 " + Integer.parseInt (str.substring(4,6))
                + "월 " + Integer.parseInt (str.substring(6,8)) + "일";

        return  temp;
    }
    /** 년월 한글로 표시한다 */
    public static String dateHanYM(String str) {

        String temp = null;
        int len = str.length();

        if (len != 6)
            return str;
        if ((str.equals("000000"))||(str.equals("    0")))
            return "";
        temp = str.substring(0,4) + "년 " + Integer.parseInt (str.substring(4,6)) + "월";

        return  temp;
    }


    /** 가변적인 String을 고정된 length의 byte[]로 변환한다. */
    /** 입력된 String이 고정된 길이보다 작을 경우 space를 추가한다. */
    /** 구분 '0':left align '1':right align */
    /** 한글 입력시 잘못 계산되는 오류 수정 */
    public static byte[] fixlengthbyte(int kind, int out_len, String str, char fillCh, String charsetName ) throws Exception {
        if (str == null) {
            return null;
        }

        if (str.length() <= 0) {
            return new byte[]{};
        }
        if (Charset.isSupported(charsetName) == false) {
            throw new Exception("charsetName parameter is invalid!!");
        }

        byte [] temp = new byte [out_len];
        for (int i=0; i<out_len; i++) {
            //temp[i] = Byte.parseByte(fillCh)   ;
            temp[i] = (byte) fillCh   ;
        }  //out_len 만큼 temp에 space를 채운다.

        if( !(str == null || str.trim().equals("") || str.equals("null") ) ) {
            byte[] input = str.getBytes(charsetName);

            int in_len = input.length;

            // 입력된 길이보다 해당 String이 긴 경우
            if (in_len > out_len){
                in_len = out_len;
                int hanNum = 0;
                while( ((int)(input[out_len-1-hanNum])) > 127 || ((int)(input[out_len-1-hanNum])) < 0) {
                    hanNum++;
                    if(out_len-1-hanNum < 0) break;
                }
                if( Math.abs( hanNum % 2 ) == 1) in_len--;
            }

            if (kind == 1) {
                for (int i = (out_len - in_len), j = 0; i < out_len; i++, j++) {
                    temp[i] = input[j];
                }
            } else
                for (int i=0; i<in_len; i++) {
                    temp[i] = input[i];
                }
        }
        return temp;
    }

    /** 가변적인 String을 고정된 length의 String으로 변환한다. */
    /** 입력된 String이 고정된 길이보다 작을 경우 space를 추가한다. */
    /** 구분 '0':left align '1':right align */
    /** 한글 입력시 잘못 계산되는 오류 수정 */
    public static String fixlength(int kind, int out_len, String str, char fillCh ) throws Exception {
        return new String(fixlengthbyte(kind, out_len, str, fillCh, "utf-8"), 0, out_len);
    }

    public static String fixlength(int kind, int out_len, String str, char fillCh, String charsetName ) throws Exception {
        return new String(fixlengthbyte(kind, out_len, str, fillCh, charsetName), 0, out_len);
    }


    /** 가변적인 String을 고정된 length의 String으로 변환한다.
     입력된 String이 고정된 길이보다 작을 경우 space를 추가한다.
     구분 '0':left align '1':right align */
    public static String fixlength(int kind, int out_len, String str) throws Exception {
        return fixlength(kind, out_len, str, ' ');
    }

    public static String fixlength(int kind, int out_len, String str, String charsetName) throws Exception {
        return fixlength(kind, out_len, str, ' ', charsetName);
    }

    /**
     * Long.parseLong 을 실행
     * @param def parsing 이 실패할 때 넘어올 값
     * @param str parsing 할 문자열
     * @return parsing 된 결과값
     */
    public static long validLong(long def, String str) {
        try {
            return Long.parseLong(str);
        } catch(Exception e) {
            return def;
        }
    }

    /**
     * Integer.parseInt 를 실행
     * @param def parsing 이 실패할 때 넘어올 값
     * @param str parsing 할 문자열
     * @return parsing 된 결과값
     */
    public static int validInt(int def, String str) {
        try {
            return Integer.parseInt(str);
        } catch(Exception e) {
            return def;
        }
    }

    /**
     * Double.parseDouble 를 실행
     * @param def parsing이 실패할 때 넘어올 값
     * @param str parsing할 문자
     * @return parsing된 결과값
     */
    public static double validDouble(double def, String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * 문자열이 Y 인지 검사함
     * @param def 검사가 실패할 때 넘어올 값
     * @param str 검사할 문자열
     * @return str 이 Y 이면 true, 그 외는 false
     */
    public static boolean validBoolean(boolean def, String str) {
        try {
            return "Y".equals(str.toUpperCase());
        } catch(Exception e) {
            return def;
        }
    }

    /**
     * 문자열이 empty 이거나 null 이면 def 로 변경함
     * @param def
     * @param str
     * @return 변경된 문자열
     */
    public static String validString(String def, String str) {
        String rValue = def;
        if(!("".equals(str) || null == str)) rValue = str;
        return rValue;
    }


    /** 주민등록번호 또는 사업자번호에 '-'를 첨가한다. */
    public static String regnNo(String str) {

        String temp = null;
        int len = str.length();

        if ((len != 13) && (len !=10))
            return str;

        // 사업자번호
        if(len == 10)
            temp = str.substring(0,3) + "-"
                    + str.substring(3,5) + "-"
                    + str.substring(5,10);

        // 주민등록번호
        if(len == 13)
            temp = str.substring(0,6) + "-"
                    + str.substring(6,13);

        return temp;
    }

    /** 입력 string을 offset에서 부터 n byte 추출.
     String method인 substring을 쓸 경우 한글을 한 character로 인식하므로
     byte로 변환한 다음 처리한다 */
    public static String substr(String str, int offset, int len) {
        String output = "";

        try {
            byte [] input = str.getBytes();
            if (offset >= input.length) return output;
            if (offset+len > input.length)
                len = input.length - offset;
            output = new String(input, offset, len);
        } catch(Exception e) {}

        return output;
    }

    /** 시분초 사이에 ':'를 첨가한다. */
    public static String time(String str) {

        String temp=null;
        // Hjun edit.. 2000.11.1
        if (str==null || (str.trim()).length()==0)
            return "";
        int len = str.length();

        if (len != 6)
            return str;

        temp = str.substring(0,2) + ":" + str.substring(2,4)
                + ":" + str.substring(4,6);

        return  temp;
    }

    /** 시분 사이에 ':'를 첨가한다. */
    public static String time2(String str) {

        String temp=null;
        if (str==null || (str.trim()).length()==0)
            return "";
        int len = str.length();
        if (len < 4)
            return str;

        temp = str.substring(0,2) + ":" + str.substring(2,4);

        return  temp;
    }

    /** 시분 한글로 표시한다 */
    /* by hjun 2000.12.06 */
    public static String timeHanHM(String str) {
        if (str==null || (str.trim()).length()==0)
            return "";
        String temp=null;
        int len = str.length();

        if (len > 6)
            return str;

        temp = Integer.parseInt(str.substring(0, 2)) + "시 " +
                Integer.parseInt(str.substring(2, 4)) + "분";

        return  temp;
    }

    public static String fixlengthZ(int out_len, String str) {

        byte [] input = str.getBytes();
        byte [] temp = new byte [out_len];

        int i,j;
        int in_len = input.length;

        for (i=0; i<out_len; i++) {
            temp[i] =(byte) ' ';
        }

        // 입력된 길이보다 해당 String이 긴 경우
        if (in_len > out_len) in_len = out_len;

        for (i=(out_len-in_len),j=0; i<out_len; i++,j++) {
            temp[i] = input[j];
        }

        String output = new String(temp, 0, out_len);

        return output;
    }

    // 중간에 간격을 공백한칸으로 유지
    public static String trimMid(String txt) {
        int p = 0;
        for(;;) {
            p = txt.indexOf("  ");
            if(p < 1) return txt.trim();
            txt = txt.substring(0, p) + txt.substring(p + 1);
        }
    }


    /**
     * 배열 속에 해당 문자열이 있는지 확인
     * @param strArray 문자열
     * @param strValue 찾는 값
     * @return true : 있음. false : 없음
     */
    public static boolean isInArray(String[] strArray, String strValue) {
        return -1 != getIndexInArray(strArray, strValue);
    }

    /**
     * 배열 속에 해당 문자열이 있는 위치를 찾음
     * @param strArray 문자열
     * @param strValue 찾는 값
     * @return -1 : 찾을 수 없음. 그 외 : 배열 속 위치
     */
    public static int getIndexInArray(String[] strArray, String strValue) {
        for(int index = 0; index < strArray.length; index++)
            if(strArray[index].equals(strValue))
                return index;

        return -1;
    }

    public static String formatTag(String tagName, Object value) {
        String result = "<" + tagName + ">";
        if(value != null) result += value.toString();
        result += "</" + tagName + ">" + NEW_LINE;
        return result;
    }

    public static String formatTag(String tagName, long value) {
        return formatTag(tagName, String.valueOf(value));
    }

    public static String formatTag(String tagName, int value) {
        return formatTag(tagName, String.valueOf(value));
    }

    public static String formatTag(String tagName, boolean value) {
        return formatTag(tagName, String.valueOf(value));
    }

    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        String regex = "^[0-9]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }

    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    public static boolean isNull(String str) {
        return isNull2(str);
    }

    public static boolean isNotNull2(String str) {
        return !isNull2(str);
    }

    public static boolean isNull2(String str) {
        if (str == null || "".equals(str) || "null".equals(str)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * XSS 제거
     * @param value
     * @return
     */
    public static String removeXSS(String value){

        value = value.replaceAll("&", "&amp;");
        value = value.replaceAll("<", "&lt;");
        value = value.replaceAll(">", "&gt;");
        value = value.replaceAll("%00", null);
        value = value.replaceAll("\"", "&#34;");
        value = value.replaceAll("\'", "&#39;");
        value = value.replaceAll("%", "&#37;");
        value = value.replaceAll("../", "");
        value = value.replaceAll("..\\\\", "");
        value = value.replaceAll("./", "");
        value = value.replaceAll("%2F", "");

        return value;
    }

    public static List<String> tokenize(String str, String delimiter) {
        if (str == null) {
            return null;
        }

        if (delimiter == null || "".equals(delimiter) ) {
            return Arrays.asList(str);
        }

        StringTokenizer st = new StringTokenizer(str, delimiter);
        List<String> lsStr = new ArrayList<String>();
        while(st.hasMoreTokens()) {
            lsStr.add(st.nextToken());
        }
        return lsStr;
    }

    public static <T> String listToString(List<T> list, String delimiter ){
        StringBuilder result = new StringBuilder();
        for( int i = 0 ; i < list.size() ; i++ ) {
            if ( i == list.size() - 1 ) {
                result.append(list.get(i));
            } else {
                result.append(list.get(i)).append(delimiter);
            }
        }
        return result.toString();
    }


    public static List<String> stringToList(String src, String delimiter){
        if (src == null) {
            return null;
        }

        if (delimiter == null) {
            delimiter = " ";
        }

        List<String> lsResult = new ArrayList<String>();

        StringTokenizer st = new StringTokenizer(src, delimiter);
        while(st.hasMoreTokens()) {
            lsResult.add(st.nextToken());
        }
        return lsResult;
    }


    public static String[] stringToArray(String src, String delimiter){
        if (src == null) {
            return null;
        }

        if (delimiter == null) {
            delimiter = " ";
        }

        // String Array로 변환
        List<String> lsResult = stringToList(src, delimiter);
        String[] arrResult = new String[lsResult.size()];
        int arrIdx = 0;
        for ( String str : lsResult ) {
            arrResult[arrIdx++] = str;
        }

        return arrResult;
    }



    public static String safeTrim(String str) {
        if (str == null) {
            return "";
        }
        return str.trim();
    }

    public static String masking(String str) {
        if (str == null) {
            return "";
        }
        StringBuffer returnStrBuffer = new StringBuffer();
        if ( str.length() == 1 ) {
            returnStrBuffer.append("*");
        } else if( str.length() == 2 ) {
            returnStrBuffer.append(str.substring(0, 1)).append("*");
        } else if( str.length() > 2 ) {
            for( int i=0 ; i < str.length(); i++ ) {
                if ( i==0 || i==str.length()-1 ) {
                    returnStrBuffer.append(str.charAt(i));
                } else {
                    returnStrBuffer.append("*");
                }
            }
        }
        return returnStrBuffer.toString();
    }

    public static int stringToInt( String s ) {
        int result = 0;
        if ( s == null) {
            return 0;
        }
        try {
            result = Integer.parseInt(s);
        } catch(Exception e) {
            return 0;
        }
        return result;
    }


    public static long stringToLong( String s ) {
        long result = 0L;
        if ( s == null) {
            return 0L;
        }
        try {
            result = Long.parseLong(s);
        } catch(Exception e) {
            return 0L;
        }
        return result;
    }

    public static boolean isMatchRegularExpression(String srcStr, String regularExpression) {
        if (srcStr == null || regularExpression == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(srcStr);
        return matcher.find();
    }


    public static String urlEncoding(String s , String encoding) {

        if ( s == null ) {
            return "";
        }

        String result = "";
        try {
            result = URLEncoder.encode(s, encoding);
            result = result.replaceAll("\\+", "%20");
        } catch(Throwable e) {
            e.printStackTrace();
        }
        return result;
    }


    public static boolean containsIgnoreCase(String str, String substring) {
        if (str == null || substring == null) {
            return false;
        }
        String smallOriginStr = str.toLowerCase();
        String smallSubStr    = substring.toLowerCase();
        return smallOriginStr.contains(smallSubStr);
    }



    public static String getStrToLimitNum(String str, int limitNumber) {
        if ( str == null ) {
            return str;
        }
        if ( limitNumber == 0 ) {
            return "";
        }
        if ( limitNumber < 0 ) {
            return str;
        }

        if ( str.length() > limitNumber ) {
            return str.substring(0, limitNumber) + ".....(The rest is omitted(이하 생략))";
        } else {
            return str;
        }
    }

    /**
     * 해당 문자열을 0부터 endIndex까지 자름
     * Surrogat관련하여 해당 영역의 문자가 중간에 잘리는 경우가 발생 
     * 이를 방지하고자 substring의 마지막 char가 HighSurrogate 인 경우 원래 자르려는 크기보다 1작게 자르는 로직 추가
     * 무조건 0부터 자르는 경우만 사용가능 
     * 범위로 자르는 경우는 앞에도 해당 문자가 중간에 잘릴 수 있으므로 처리하는 로직 필요
     * 
     * 
     * @param value
     * @param endIndex
     * @return
     */
    public static String safeSubString(String value, int endIndex) {
        try{
            if(endIndex > value.length()){
                endIndex = value.length();
            }
            value = value.substring(0, endIndex);
            
            if(endIndex < 2){
                char c = value.charAt(value.length() - 1);
                if(Character.isHighSurrogate(c)){
                    value = "";
                }
            }else{
                char c = value.charAt(value.length() - 1);
                if(Character.isHighSurrogate(c)){
                    // 문제가 되면 재귀호출을 통해 마지막이  HighSurrogate가 아닐때까지로 수정해야 함
                    // value = subString(value, endIndex-1);
                    value = value.substring(0, endIndex - 1);
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
        return value;
    }

    /**
     * 문자배열 만들기
     * @param strs
     * @return
     */
    public static String[] getStringArray(String... strs) {
        if (strs.length == 0) {
            return null;
        }
        List<String> Arr = new ArrayList<>();
        for (String str : strs) {
            Arr.add(str);
        }
        return Arr.toArray(new String[Arr.size()]);
    }

    /**
     * 누적 문자열 만들기
     * @param accumulateCnt
     * @param standardCnt
     * @return
     */
    public static String concatAccumulateString(int accumulateCnt, int standardCnt) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(accumulateCnt);
        buffer.append("/");
        buffer.append(standardCnt);

        return buffer.toString();
    }

    public static String concatAccumulateString2(int accumulateCnt, int failAccumulateCnt, int standardCnt, int failStandardCnt) {
        StringBuilder buffer = new StringBuilder();

        buffer.append(accumulateCnt + "("+ failAccumulateCnt +")");
        buffer.append("/");
        buffer.append(standardCnt + "(" + failStandardCnt + ")");

        return buffer.toString();
    }

    public static String joinStrings(String separator, String... strs) {
        StringJoiner sj = new StringJoiner(separator);
        if (PredicateUtils.isNotNull(strs)) {
            for (String str : strs) {
                sj.add(str);
            }
        }
        return sj.toString();
    }

    public static String joinStringsNoSeparator(String... strs) {
        StringBuffer sb = new StringBuffer();
        if (PredicateUtils.isNotNull(strs)) {
            for (String str : strs) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    /**
     * 비밀번호 검증 (문자열, 숫자, 특수문자 포함한 minSize ~ maxSize) 정규식
     * @param password
     * @param minSize
     * @param maxSize
     * @return 통과 : true, 통과실패 : false
     */
    public static boolean isValidPassword(String password, int minSize, int maxSize) {
        final String REGEX = "^((?=.*\\d)(?=.*[a-zA-Z])(?=.*[\\W]).{" + minSize + "," + maxSize + "})$";
        Matcher matcher = Pattern.compile(REGEX).matcher(password);
        return matcher.find();
    }

    public static String convertNullToEmptyString(String value) {
        return (value == null) ? "" : value;
    }

    public static String convertEmptyToNullInteger(Integer value) {
        return (value == null) ? "" : String.valueOf(value);
    }

    /**
     * 문자열을 16진수로 변환
     * @param str
     * @return
     */
    public static String convertStringToHex(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] charArray = str.toCharArray();
        for (char c : charArray) {
            String charToHex = Integer.toHexString(c);
            stringBuilder.append(charToHex);
        }
        return stringBuilder.toString();
    }

    /**
     * 16진수를 문자열로 변환
     * @param hexStr
     * @return
     */
    public static String convertHexToString(String hexStr) {
        String HexString = hexStr;
        char[] Temp_Char = HexString.toCharArray();
        String outputString = new String();
        for(int x = 0; x < Temp_Char.length; x = x+2) {
            String Temp_String = "" + Temp_Char[x] + "" + Temp_Char[x+1];
            char character = (char)Integer.parseInt(Temp_String, 16);
            outputString = outputString + character;
        }
        return outputString;
    }

    public static String removeDoubleQuotes(String str) {
        if (str != null || !"".equals(str)) {
            return str.replace("\"", "");
        }
        return null;
    }

    /**
     * 문자열에 containCheckList 배열의 문자열이 포함되어있는지 확인
     * @param jsonStr
     * @param containCheckList
     * @return
     */
    public static boolean isContainsAsStringList(String jsonStr, List<String>containCheckList) {
        if (jsonStr != null && containCheckList.size() > 0) {
            for (String str : containCheckList) {
                if (jsonStr.contains(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> getAllNodeKeys(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Map<String, Object> treeMap = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
            return findKeys(treeMap, new ArrayList<>());
        } catch (JsonProcessingException jpe) {
            System.out.println(jpe.getMessage());
        }
        return null;
    }

    private static List<String> findKeys(Map<String, Object> treeMap, List<String> keys) {
        treeMap.forEach((key, value) -> {
            if (value instanceof LinkedHashMap) {
                LinkedHashMap map = (LinkedHashMap) value;
                findKeys(map, keys);
            } else if (value instanceof List) {
                ArrayList list = (ArrayList) value;
                list.forEach(map -> findKeys((LinkedHashMap) map, keys));

            }
            keys.add(key);
        });

        return keys;
    }

    public static boolean hasDuplicates(List<String> arr1, List<String> arr2) {
        HashSet<String> set = new HashSet<>();
        for (String str : arr1) {
            set.add(str);
        }
        for (String str : arr2) {
            if (set.contains(str)) {
                return true; // 중복 발견
            }
        }
        return false; // 중복 없음
    }

    public static HttpUrl buildUrlWithParamsByMap(String baseUrl, Map<String, String> params) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return urlBuilder.build();
    }

    public static String  buildUrlWithParamsByModel(String baseUrl, Object obj) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(baseUrl).newBuilder();
        StringJoiner queryParams = new StringJoiner("&");

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    if (!field.getName().equals("serialVersionUID")) {
                        queryParams.add(
                                URLEncoder.encode(field.getName(), "UTF-8") + "=" +
                                URLEncoder.encode(value.toString(), "UTF-8")
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return urlBuilder + "?" + queryParams;
    }

    public static void main(String[] args) {
        //boolean is = containsIgnoreCase("S000001", "S");
//        List<String> list = Arrays.asList("1", "2", "3");
//        System.out.println(">> " + listToString(list, ","));
    }

}

