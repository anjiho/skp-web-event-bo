package kr.co.syrup.adreport.service.adreport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import kr.co.syrup.adreport.framework.filters.TransactionContextHolder;
import kr.co.syrup.adreport.framework.filters.TransactionTrackingFilter;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.JsonUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.model.adreport.AdreportResponseWrapDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

@Slf4j
@Service
public class ApiHelperService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AES256Utils aesUtils;

    public String encryptMdn(String plainMdn) {
        if (StringTools.isNull2(plainMdn)) {
            return "";
        } else {

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String ranDate = dateFormat.format(calendar.getTime()) + RandomStringUtils.randomNumeric(3);

            //return aesUtils.encrypt(plainMdn + "_" + ranDate);
            return aesUtils.encrypt(plainMdn + "_" + ranDate);
        }
    }

    public String decryptMdn(String encryptMdn) {
        if (StringTools.isNull2(encryptMdn)) {
            return "";
        } else {
            String decMdn = aesUtils.decrypt(encryptMdn);
            String retMdn = decMdn.split("_")[0];
            return retMdn;
        }
    }

    private Map<String, Object> convertToMap(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<Map>() {
        });
    }

    private void loggingRunTime(String url, String methodType, Object reqParams, Object response, Object addHeaders,  double totalTimeSeconds) {
        StringBuilder builder = new StringBuilder();
        builder.append("\n***********************************");
        builder.append("\n*** HttpRequest API CALL RESULT ***");
        builder.append("\n= URL : ").append(url);
        builder.append("\n= METHOD : ").append(methodType);
        try {
            if (addHeaders != null) {
                builder.append("\n= REQ HEADERS : ").append(StringTools.getStrToLimitNum(JsonUtils.writeValueAsString(addHeaders), 1000));
            } else {
                builder.append("\n= REQ HEADERS : ");
            }
            if (reqParams != null) {
                builder.append("\n= REQ : ").append(StringTools.getStrToLimitNum(JsonUtils.writeValueAsString(reqParams), 1000));
            } else {
                builder.append("\n= REQ : ");

            }
            if (response != null) {
                builder.append("\n= RES : ").append(StringTools.getStrToLimitNum(JsonUtils.writeValueAsString(response), 1000));
            } else {
                builder.append("\n= RES : ");
            }
        } catch (Exception e) {
        }
        builder.append("\n= TOTAL RUN TIME : ").append(totalTimeSeconds).append("sec");
        builder.append("\n***********************************");

        log.info(builder.toString());
    }

    public <T> T callPostApi(String url, Object paramCondition, Class<T> resClass) {
//        T res = callGetApi4Internal(url, paramCondition, addHeaders, resClass, responseDataClass);
        T res = callPostApi4Internal(url, paramCondition, null, resClass, null);
        return res;
    }

    public <T> T callPostApi(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass) {
//        T res = callGetApi4Internal(url, paramCondition, addHeaders, resClass, null);
        T res = callPostApi4Internal(url, paramCondition, addHeaders, resClass, null);
        return res;
    }

    public <T> T callPostApi(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass, Class<?> responseDataClass) {
//        T res = callGetApi4Internal(url, paramCondition, addHeaders, resClass, responseDataClass);
        T res = callPostApi4Internal(url, paramCondition, addHeaders, resClass, responseDataClass);
        return res;
    }

    private <T> T callPostApi4Internal(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass, Class<?> responseDataClass) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T res = null;

        try {
            HttpResponse<?> result = null;
            HttpRequestWithBody httpRequest = Unirest.post(url);

            if (addHeaders != null) {
                httpRequest.headers(addHeaders);
            }

            result = httpRequest.body(paramCondition).asObject(resClass);

            res = (T) result.getBody();
            log.info("request: {}", httpRequest.toString());

        } catch (Exception e) {
            String paramsString = "";

            try {
                paramsString = JsonUtils.writeValueAsString(paramCondition);
            } catch (Exception e1) {
                log.error("json string 변환 오류 : {}", e1.getMessage(), e);
            }
            log.error("API 호출 오류 : {}, url : {}, params: {}", e.getMessage(), url, paramsString, e);
        } finally {
            stopWatch.stop();
            loggingRunTime(url, "POST", paramCondition, res, addHeaders, stopWatch.getTotalTimeSeconds());
        }

        try {
            if (responseDataClass != null && res != null && res instanceof AdreportResponseWrapDto) {

                AdreportResponseWrapDto adreportResponseWrapDto = (AdreportResponseWrapDto) res;

                if (adreportResponseWrapDto.getData() != null) {
                    adreportResponseWrapDto.setData(JsonUtils.changeObject(adreportResponseWrapDto.getData(), responseDataClass));
                    //res = (T) adreportResponseWrapDto;
                }
            }
        } catch (Exception ee) {
            //
        }

        return res;
    }

    public <T> T callGetApi(StringBuilder url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass) {
        T res = callGetApi4Internal(url.toString(), paramCondition, addHeaders, resClass, null);
        return res;
    }

    public <T> T callGetApi(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass) {
        T res = callGetApi4Internal(url, paramCondition, addHeaders, resClass, null);
        return res;
    }

    public <T> T callGetApi(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass, Class<?> responseDataClass) {
        T res = callGetApi4Internal(url, paramCondition, addHeaders, resClass, responseDataClass);
        return res;
    }

    private <T> T callGetApi4Internal(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass, Class<?> responseDataClass) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T res = null;

        try {
            Map<String, Object> paramsMap = convertToMap(paramCondition);
            HttpResponse<?> result = null;
            HttpRequest httpRequest = Unirest.get(url) //
                    .queryString(paramsMap);

            if (addHeaders != null) {

                // 모든 요청시 트랜잭션 ID를 같이 보내도록 - start -
                // 참조 TransactionIdRequestCallback
                String transactionId = TransactionContextHolder.getTransactionId();
                if (StringUtils.hasText(transactionId)) {
                    addHeaders.put(TransactionTrackingFilter.TRANSACTION_ID_HEADER, transactionId);
                }
                // 모든 요청시 트랜잭션 ID를 같이 보내도록 - end -

                httpRequest.headers(addHeaders);
            }

            result = httpRequest.asObject(resClass);

            res = (T) result.getBody();

        } catch (Exception e) {
            String paramsString = "";

            try {
                paramsString = JsonUtils.writeValueAsString(paramCondition);
            } catch (Exception e1) {
                log.error("json string 변환 오류 : {}", e1.getMessage(), e);
            }
            log.error("API 호출 오류 : {}, url : {}, params: {}", e.getMessage(), url, paramsString, e);
        } finally {
            stopWatch.stop();
            loggingRunTime(url, "GET", paramCondition, res, addHeaders, stopWatch.getTotalTimeSeconds());
        }

        try {
            if (responseDataClass != null && res != null && res instanceof AdreportResponseWrapDto) {

                AdreportResponseWrapDto adreportResponseWrapDto = (AdreportResponseWrapDto) res;

                if (adreportResponseWrapDto.getData() != null) {
                    adreportResponseWrapDto.setData(JsonUtils.changeObject(adreportResponseWrapDto.getData(), responseDataClass));
                    //res = (T) adreportResponseWrapDto;
                }
            }
        } catch (Exception ee) {
            //
        }

        return res;
    }
}
