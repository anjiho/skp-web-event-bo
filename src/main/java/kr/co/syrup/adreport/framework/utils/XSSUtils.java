package kr.co.syrup.adreport.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;


/**
 * @author skplanet
 *
 */
@Slf4j
public abstract class XSSUtils {


    /**
     * 현재 전체 문자열 필터링 사용되고 있는 메소드
     * @param value 필터링 하고자 하는 문자열
     * @return 필터된 문자열
     */
    public static String stripXSS(String value) {

        String filteredValue    = replaceSpecialTags(value);
        filteredValue           = replaceSpecialChar(filteredValue);

        //log.debug("value : [{}], valuefilteredValue : [{}]", value, filteredValue);

        return filteredValue;
    }


    public static String replaceSpecialChar(String value) {
        if (!StringUtils.hasLength(value)) {
            return value;
        }
//        value = value.replaceAll("&"   , "&amp;"    );
//        value = value.replaceAll("#"   , "&#35;"    );
        value = value.replaceAll("\""  , "&quot;"   );
        value = value.replaceAll("\'"  , "&#x27;"   );
//        value = value.replaceAll("\\(" , "&#40;"    );
//        value = value.replaceAll("\\)" , "&#41;"    );
        value = value.replaceAll("<"   , "&lt;"     );
        value = value.replaceAll(">"   , "&gt;"     );

        return value;
    }


    public static final Pattern[] PATTERNS = new Pattern[]{
            Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE)
            , Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
            , Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
            , Pattern.compile("</script>", Pattern.CASE_INSENSITIVE)
            , Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
            , Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
            , Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
            , Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE)
            , Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE)
            , Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
            , Pattern.compile("file://[\\w|/]*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };


    private static String replaceSpecialTags(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            value = value.replaceAll("", "");

            for( Pattern pattern : PATTERNS ) {
                value = pattern.matcher(value).replaceAll("");
            }
        }
        return value;
    }

    public static void main(String[] args) {
        System.out.println(stripXSS("<script/>"));
    }
}
