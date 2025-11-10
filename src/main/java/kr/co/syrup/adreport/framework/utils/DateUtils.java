package kr.co.syrup.adreport.framework.utils;

import kr.co.syrup.adreport.web.event.define.EventLogPvKeyDefine;
import kr.co.syrup.adreport.web.event.define.SmsAuthMenuDefine;
import kr.co.syrup.adreport.web.event.entity.WebEventSmsAuthEntity;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DateUtils {
    public static final String PATTERN_YYYY_MMD_DD = "yyyy-MM-dd";

    public static final String PATTERN_YYYY_MMD_DD_HH = "yyyy-MM-dd HH";

    public static final String PATTERN_YYYYMMDDD = "YYYYMMdd";

    public static final String PATTERN_MMDD = "MMdd";

    public static final String PATTERN_HH = "HH";

    public static String dateHanYYMM(String str) {

        String temp = null;
        int len = str.length();

        if (len != 8)
            return str;
        if ((str.equals("00000000"))||(str.equals("    0")))
            return "";
        temp = str.substring(2,4) + "년 " + Integer.parseInt (str.substring(4,6)) + "월 ";

        return  temp;
    }

    public static DateTime getFormatedDT(String sDate, String sDateFormat) {
        return DateTime.parse(sDate, DateTimeFormat.forPattern(sDateFormat));
    }

    public static String getNow(String sDateFormat) {
        return DateTimeFormat.forPattern(sDateFormat).print(DateTime.now());
    }

    public static String getMaxDayOfMonth(String sDate, String sDateFormat) {
        DateTime dateTime = getFormatedDT(sDate, sDateFormat);
        return DateTimeFormat.forPattern("yyyyMMdd").print(dateTime.dayOfMonth().withMaximumValue());
    }

    public static Date returnNowDate() {
        Date today = new Date();
        return today;
    }

    public static String getNowHour() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("HH");
        return sdf.format(today);
    }

    public static String getNowDay() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(today);
    }

    public static String getNowMMDD() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("MMdd");
        return sdf.format(today);
    }

    public static String getNowYYMMDD() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyMMdd");
        return sdf.format(today);
    }

    public static String getNowYYMMDDHH() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyMMddHH");
        return sdf.format(today);
    }

    /**
     * yyyymmdd ==> yyy-mm-dd 변환
     */
    public static String convertDateFormat(String date) {
        String newDate = "";
        SimpleDateFormat fromDateFormat = new SimpleDateFormat("yyyymmdd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        try {
            Date originDate = fromDateFormat.parse(date);
            newDate = dateFormat.format(originDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return newDate;
    }

    public static String convertDateToString(Date date, String pattern) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern)
                .withZone(ZoneId.systemDefault());
        String dateToStr = format.format(date.toInstant());
        return dateToStr;
    }

    public static String convertDateFormat2(String date) {
        String newDate = "";
        SimpleDateFormat fromDateFormat = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmdd");
        try {
            Date originDate = fromDateFormat.parse(date);
            newDate = dateFormat.format(originDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return newDate;
    }

    public static Date convertDateTimeFormat(String yyyymmdd) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date to = new Date();
        try {
            to = transFormat.parse(yyyymmdd);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return to;
    }

    public static Date convertDateTimeFormat2(String yyyymmdd) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date to = new Date();
        try {
            to = transFormat.parse(yyyymmdd);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return to;
    }

    public static Date convertDateTimeFormat3(String yyyymmdd) {
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
        Date to = new Date();
        try {
            to = transFormat.parse(yyyymmdd);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return to;
    }

    public static String returnNowDateByYyyymmddhhmmss() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(today);
    }

    /**
     * 특정일 기준 현재일과 차이 일수 구하기
     * @param paramDate
     * @return
     */
    public static int differenceToday(String paramDate) {
        Calendar getToday = Calendar.getInstance();
        getToday.setTime(new Date()); //금일 날짜

        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(paramDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //특정 일자

        long diffSec = (getToday.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24*60*60); //일자수 차이

        return (int)diffDays;
    }

    public static String plusDay(String yyyy_mm_dd, String type, int plusDay) {
        String plusDate = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dateFormat.parse(yyyy_mm_dd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, plusDay);
        String strDate = dateFormat.format(cal.getTime());
        String[] str = strDate.split("-");
        if ("MM-DD".equals(type)) {
            plusDate = str[1]+"-"+str[2];
        } else if ("YYYY".equals(type)) {
            plusDate = str[0];
        } else if ("YYYYMMDD".equals(type)) {
            plusDate = strDate;
        } else if ("MM.DD".equals(type)) {
            plusDate = str[1]+"."+str[2];
        } else if ("YYYY.MM.DD".equals(type)) {
            plusDate = str[0] + "." + str[1] + "." + str[2];
        }
        return plusDate;
    }

    /**
     * yyyy-MM-dd HH:mm:ss > yyyy-MM-dd, HH 분리
     * @param dateTime
     * @return
     */
    public static Map<String, String> disuniteDateHourFromYYYYMMDDHHMMSS(String dateTime) {
        if (PredicateUtils.isNotNull(dateTime)) {
            Map<String, String> disuniteDateTimeMap = new HashMap<>();
            disuniteDateTimeMap.put("date", convertDateToString(convertDateTimeFormat(dateTime), PATTERN_YYYY_MMD_DD));
            disuniteDateTimeMap.put("hour", convertDateToString(convertDateTimeFormat(dateTime), PATTERN_HH));
            return disuniteDateTimeMap;
        }
        return null;
    }

    public static String returnNowDateByYyyymmddhhmmssMilliSecond() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return sdf.format(today);
    }

    public static String returnNowDateByYYmmddhhmmss() {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("yyMMddHHmmss");
        return sdf.format(today);
    }

    public static String returnNowDateByPattern(String pattern) {
        Date today = new Date();
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat(pattern);
        return sdf.format(today);
    }

    public static int differenceTwoDay(String paramDate1, String paramDate2) {
        //Calendar getToday = Calendar.getInstance();
        //getToday.setTime(new Date()); //금일 날짜

        Date date = new Date();
        Date date2 = new Date();
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(paramDate1);
            date2= new SimpleDateFormat("yyyy-MM-dd").parse(paramDate2);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //특정 일자

        Calendar cmpDate2 = Calendar.getInstance();
        cmpDate2.setTime(date2); //특정 일자

        long diffSec = (cmpDate2.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24*60*60); //일자수 차이

        return (int)diffDays;
    }

    public static String plusMinute(int plusMinute) {
        String plusDate = "";
        Date date = new Date();
        // 포맷변경 ( 년월일 시분초)
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, plusMinute);
        return sdFormat.format(cal.getTime());

    }

    public static void main(String[] args) {
//        String date1 = "20221005";
//        System.out.println(convertDateTimeFormat3(date1));
        //plusMinute(3);
        System.out.println("entity > " + returnNowDateByPattern(PATTERN_YYYYMMDDD));
    }
}
