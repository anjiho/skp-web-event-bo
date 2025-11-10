package kr.co.syrup.adreport.framework.filters;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import kr.co.syrup.adreport.framework.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class CustomPatternLayout extends PatternLayout {


    private Pattern multilinePattern;
    private List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(maskPatterns.stream().collect(Collectors.joining("|")), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    /**
     * logback masking 처리된 문자열 반환
     * SS-20462 [SODAR] LOGX 개인정보 포함 여부 확인 및 조치
     *
     * @param message 로그에 찍힌 전체 메시지 라인
     * @return String 개인정보 masking 처리하여 문자열 라인 반환
     */
    private String maskMessage(String message) {

        if (multilinePattern == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder(message); // 로그에 찍힌 메세지 라인 StringBuilder에 담기
        Matcher matcher = multilinePattern.matcher(sb); // Matcher >> logback maskPattern 정규식 패턴

        try {
            while (matcher.find()) {
                IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                    if (matcher.group(group) != null) {
                        //핸드폰번호 마스킹
                        if (isIndexOf(matcher.group(),"phoneNumber")
                                || isIndexOf(matcher.group(), "phone")
                                || isIndexOf(matcher.group(), "number")
                                || isIndexOf(matcher.group(), "mdn")
                                || isIndexOf(matcher.group(), "userPhoneNumber")
                                || isIndexOf(matcher.group(), "receiverPhoneNumber")
                                || isIndexOf(matcher.group(), "attendValue")
                        ) {
                            int strLen = (matcher.end(group) - matcher.start(group));
                            if (strLen >= 11) {
                                IntStream.range(matcher.start(group), matcher.end(group)).forEach(
                                        i -> {
                                            IntStream.range((matcher.start(group) + 3), (matcher.start(group) + 7)).forEach(
                                                    j -> sb.setCharAt(j, '*')
                                            );
                                        }
                                );
                            }
                        } else if (isIndexOf(matcher.group(), "userName")) {
                            String userName = sb.substring(matcher.start(group), matcher.end(group));
                            if (StringTools.isNotNull2(userName)) {
                                if (userName.indexOf("@") > 0) {
                                    maskEmailPattern(matcher, sb, group);
                                } else {
                                    maskUserName4IaPattern(matcher, sb, group);
                                }
                            }
                        } else if (isIndexOf(matcher.group(), "name")
                                || isIndexOf(matcher.group(), "userNm")
                        ) {
                            IntStream.range(matcher.start(group), matcher.end(group)).forEach(
                                    i -> {
                                        IntStream.range(( matcher.start(group) + 1 ), (matcher.end(group) - 1) ).forEach(
                                                j -> sb.setCharAt(j, '*')
                                        ) ;
                                    }
                            );
                        } else if (isIndexOf(matcher.group(), "email")) {
                            maskEmailPattern(matcher, sb, group);
                        } else {
                            //그외 마스킹 패턴은 전부 '*' 처리
                            IntStream.range(matcher.start(group), matcher.end(group)).forEach(
                                    i -> sb.setCharAt(i, '*')
                            );
                        }
                    }
                });
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return message;
        }
    }

    private boolean isIndexOf(String matcherGroup, String matchingTarget) {
        if (matcherGroup == null || matchingTarget == null) {
            return false;
        }

        if (matcherGroup.toLowerCase().indexOf(matchingTarget.toLowerCase()) > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void maskEmailPattern(Matcher matcher, StringBuilder sb, int group) {
        //"key" : "value" 이메일 형식 값을 ":" 값으로 split ( ex > "email" : "test@test.com" )
        String emailValue = sb.substring(matcher.start(group), matcher.end(group));
        if (StringTools.isNotNull2(emailValue) && emailValue.indexOf("@") > 0) {
            // "value" 값 주입 ( ex > test@test.com )
            // "value" 값을 "@" 값으로 split
            String[] emailValueSplit = StringUtils.split(emailValue, "@");
            //이메일 도멘이 제거된 이메일 아이디 값
            String findEmailId = emailValueSplit[0];

            //이메일 아이디 값의 마지막 자리수에서 앞 2자리 기준값
            int start = matcher.start(group) + findEmailId.length() - 2;
            int end = matcher.end(group) - emailValueSplit[1].length() - 1;
            //마지막 자리수에서 앞 2자리 기준값 ~ 마지막 자리수 까지 '*' 처리
            IntStream.range(start, end).forEach(
                    i -> sb.setCharAt(i, '*')
            );
        } else {
            IntStream.range(matcher.end(group) - 2, matcher.end(group)).forEach(
                    i -> sb.setCharAt(i, '*')
            );
        }
    }

    private void maskUserName4IaPattern(Matcher matcher, StringBuilder sb, int group) {
        //마지막 자리수에서 앞 2자리 기준값 ~ 마지막 자리수 까지 '*' 처리
        IntStream.range(matcher.start(group), matcher.end(group)).forEach(
                i -> {
                    IntStream.range(( matcher.end(group) - 2 ), matcher.end(group)).forEach(
                            j -> sb.setCharAt(j, '*')
                    );
                }
        );
    }
}