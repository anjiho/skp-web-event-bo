package kr.co.syrup.adreport.framework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javassist.expr.Instanceof;
import kr.co.syrup.adreport.web.event.define.EventTypeDefine;
import kr.co.syrup.adreport.web.event.define.WinningTypeDefine;
import kr.co.syrup.adreport.web.event.dto.request.GiveAwayDeliveryButtonAddInputDto;
import kr.co.syrup.adreport.web.event.dto.response.ArEventWinningButtonAddResDto;
import kr.co.syrup.adreport.web.event.dto.response.FutureSenseApiResDto;
import kr.co.syrup.adreport.web.event.entity.ArEventNftBannerEntity;
import kr.co.syrup.adreport.web.event.entity.ArEventWinningButtonAddEntity;
import kr.co.syrup.adreport.web.event.session.TimedThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.cglib.core.CollectionUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class EventUtils {

    /**
     * 참여코드 랜덤 값 가져오기(신규)
     * @param strDigit
     * @param rowNum
     * @return
     */
    public static List<String> getRandomAttendCode(int strDigit, int rowNum) {
        if(strDigit > 0) {
            Set<String> randomCodeSet = new HashSet<>();

            for (int i=0; i<rowNum; i++) {
                String randomAttendCode = RandomStringUtils.randomAlphanumeric(strDigit).toUpperCase();
                randomCodeSet.add(randomAttendCode);
            }
            //중복된 개수
            long duplicateListCount = randomCodeSet.stream().distinct().count();

            Set<String> duplicateSet = new HashSet<>();

            //중복된 개수가 있으면
            if ((int)duplicateListCount > 0) {
                List<String> duplicateList = randomCodeSet
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());

                duplicateSet = new HashSet<>(duplicateList);

                //전체 개수 - 중복된 개수
                int reCount = (rowNum - (int)duplicateListCount);
                //전체 개수 - 중복된 개수 만큼
                for (int i=0; i<reCount; i++) {

                    String randomAttendCode = RandomStringUtils.randomAlphanumeric(strDigit).toUpperCase();

                    //중복제거
                    if (!duplicateSet.contains(randomAttendCode)) {
                        duplicateSet.add(randomAttendCode);
                    } else if (duplicateSet.contains(randomAttendCode)) {
                        //중복된 값이 있으면 i--
                        i--;
                    }
                }
            }
            return  new ArrayList<>(duplicateSet);
        }
        return null;
    }

    /**
     * 참여코드 랜덤 값 가져오기(기존의 있는 랜덤 값 비교)
     * @param strDigit
     * @param rowNum
     * @param exitsCodeList
     * @return
     */
    public static List<String> getRandomAttendCode(int strDigit, int rowNum, LinkedList<String>exitsCodeList) {
        if(strDigit > 0) {

            Set<String> exitsCodeSet = new HashSet<>(exitsCodeList);
            Set<String> newCodeSet = new HashSet<>();

            for (int i=0; i<(rowNum)  ; i++) {

                String randomAttendCode = RandomStringUtils.randomAlphanumeric(strDigit).toUpperCase();
                //중복제거
                if (!exitsCodeSet.contains(randomAttendCode)) {
                    if (!newCodeSet.contains(randomAttendCode)) {
                        newCodeSet.add(randomAttendCode);
                    } else if (newCodeSet.contains(randomAttendCode)) {
                        i--;
                    }
                } else if (exitsCodeSet.contains(randomAttendCode)) {
                    if (newCodeSet.contains(randomAttendCode)) {
                        //중복된 값이 있으면 i--
                        i--;
                    } else if (!newCodeSet.contains(randomAttendCode)) {
                        newCodeSet.add(randomAttendCode);
                    }
                }
            }
            return new ArrayList<>(newCodeSet);
        }
        return null;
    }

    /**
     * 확률 구하기
     * @param percent
     * @return
     */
    public static boolean percent(double percent) {
        return percent > ThreadLocalRandom.current().nextDouble(0, 100);
    }

    /**
     * 기준 숫자 구간 포함여부 비교
     * @param min
     * @param standard
     * @param max
     * @return
     */
    public static boolean isTwoIntSection(int min, int standard, int max) {
        if ((min <= standard) && (max >= standard)) {
            return true;
        }
        return false;
    }

    public static String traceNo(String reqTraceNo) {
        if (StringUtils.isEmpty(reqTraceNo)) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String traceNo = dateFormat.format(calendar.getTime()) + RandomStringUtils.randomNumeric(3);
            log.info("traceNo {} ", traceNo);
            return traceNo;
        }
        log.info("traceNo {} ", reqTraceNo);
        return reqTraceNo;
    }

    /**
     * URL host 명 변경
     * @param originalUrl
     * @param newHost
     * @return
     */
    public static String replaceUriHost(String originalUrl, String newHost) {
        if (StringUtils.isNotEmpty(originalUrl)) {
            try {
                return new URIBuilder(URI.create(originalUrl)).setHost(newHost).build().toString();
            } catch (URISyntaxException uriSyntaxException) {
                uriSyntaxException.printStackTrace();
            }
        }
        return null;
    }

    /**
     * url 값 http > https 로 변환
     * @param url
     * @return
     */
    public static String convertHttpsFromHttp(String url) {
        if(url.startsWith("http://") && url.indexOf("localhost") < 0) {
            return url.replaceAll("http://", "https://");
        }
        return url;
    }

    public static int calculateAgeForKorean(String ssn) { // ssn의 형식은 yyyymmdd 임
        String today = ""; // 오늘 날짜
        int manAge = 0; // 만 나이

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        today = formatter.format(new Date()); // 시스템 날짜를 가져와서 yyyyMMdd 형태로 변환

        // today yyyyMMdd
        int todayYear = Integer.parseInt(today.substring(0, 4));
        int todayMonth = Integer.parseInt(today.substring(4, 6));
        int todayDay = Integer.parseInt(today.substring(6, 8));

        int ssnYear = Integer.parseInt(ssn.substring(0, 4));
        int ssnMonth = Integer.parseInt(ssn.substring(4, 6));
        int ssnDay = Integer.parseInt(ssn.substring(6, 8));

        manAge = todayYear - ssnYear;

        if (todayMonth < ssnMonth) { // 생년월일 "월"이 지났는지 체크
            manAge--;
        } else if (todayMonth == ssnMonth) { // 생년월일 "일"이 지났는지 체크
            if (todayDay < ssnDay) {
                manAge--; // 생일 안지났으면 (만나이 - 1)
            }
        }

        return manAge; // 한국나이를 측정하기 위해서 +1살 (+1을 하지 않으면 외국나이 적용됨)
    }

    /**
     * 문자열에 특수문자가 포함되어 있는지 여부 확인
     * @param str
     * @return
     */
    public static boolean isSpecialCharacter(String str) {
        for(int i=0; i<str.length(); i++) {
            if (String.valueOf(str.charAt(i)).matches("[^a-zA-Z0-9]")) { // 특수문자 인 경우
                return true;
            }
        }
        return false;
    }

    public static boolean isPhoneNumber(String phoneNumber) {
        Pattern pattern = Pattern.compile("\\d{3}\\d{4}\\d{4}");
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            //System.out.println("Valid phone number: " + number);
            return true;
        }
        return false;
    }

    public static <T> LinkedList<T> convertALtoLL(List<T> arrayList) {
        return arrayList.stream()
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static String getTraceNo() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(calendar.getTime()) + RandomStringUtils.randomNumeric(3);
    }

    public static int getPagingStartNumber(int sPage, int pageInList) {
        int page_cnt = pageInList;
        int srow = page_cnt * (sPage - 1) + 1;

        int start = srow - 1;
        return start;
    }

    public static void main(String[] args) {
        List<String> requestTrEventIdList = Arrays.asList("1", "2");
        List<String> savedTrEventIdList = Arrays.asList("2", "4");
        boolean isDuplicates = StringTools.hasDuplicates(requestTrEventIdList, savedTrEventIdList);
        log.info("isDuplicates : {}", isDuplicates);
    }

}
