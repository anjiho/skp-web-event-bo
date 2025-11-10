package kr.co.syrup.adreport.web.event.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kr.co.syrup.adreport.framework.filters.TransactionContextHolder;
import kr.co.syrup.adreport.framework.filters.TransactionTrackingFilter;
import kr.co.syrup.adreport.framework.utils.JsonUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;
import org.apache.commons.lang.CharSet;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.*;

@Slf4j
@Service
public class OkHttpService {

    /**
     * OKHttp3 GET 콜
     *
     * @param url
     * @param addHeaders
     * @param resClass
     * @param <T>
     * @return
     */
    public <T> T callGetApi(String url, Map<String, String> addHeaders, Class<T> resClass) {
        OkHttpClient okHttpClient = getUnsafeOkHttpClient(url);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 모든 요청시 트랜잭션 ID를 같이 보내도록 - start -
        // 참조 TransactionIdRequestCallback
        String transactionId = TransactionContextHolder.getTransactionId();
        if (StringUtils.hasText(transactionId)) {
            addHeaders.put(TransactionTrackingFilter.TRANSACTION_ID_HEADER, transactionId);
        }
        // 모든 요청시 트랜잭션 ID를 같이 보내도록 - end -

        Headers headerBuild = Headers.of(addHeaders);
        Request request = new Request.Builder()
                .url(url)
                .method(HttpMethod.GET.name(), null)
                .headers(headerBuild)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                ResponseBody body = response.body();

                if (body != null) {
                    Gson gson = new Gson();
                    String jsonTxt = body.string();
                    body.close();

                    log.info("response: {}", jsonTxt);

                    JsonObject resultJsonObj = new Gson().fromJson(jsonTxt, JsonObject.class);
                    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    return objectMapper.readValue(resultJsonObj.toString(), resClass);
                }
            }

        } catch (Exception e) {
            log.error("API 호출 오류 : {}, url : {}, params: {}", e.getMessage(), url, null, e);
        } finally {
            stopWatch.stop();
            loggingRunTime(url, HttpMethod.GET.name(), null, response, addHeaders, stopWatch.getTotalTimeSeconds());
        }
        return null;
    }

    /**
     * OKHttp3 POST 콜
     *
     * @param url
     * @param paramCondition
     * @param addHeaders
     * @param resClass
     * @param <T>
     * @return
     */
    public <T> T callPostApi(String url, Object paramCondition, Map<String, String> addHeaders, Class<T> resClass) {
        OkHttpClient okHttpClient = getUnsafeOkHttpClient(url);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 모든 요청시 트랜잭션 ID를 같이 보내도록 - start -
        // 참조 TransactionIdRequestCallback
        String transactionId = TransactionContextHolder.getTransactionId();
        if (StringUtils.hasText(transactionId)) {
            addHeaders.put(TransactionTrackingFilter.TRANSACTION_ID_HEADER, transactionId);
        }
        // 모든 요청시 트랜잭션 ID를 같이 보내도록 - end -

        Headers headerBuild = Headers.of(addHeaders);
        MediaType mediaType = MediaType.parse(org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE);
        RequestBody requestBody = null;

        if (paramCondition != null) {

            String paramJson = new Gson().toJson(paramCondition);
            requestBody = RequestBody.create(paramJson, mediaType);

        }

        Request request = new Request.Builder()
                .url(url)
                .method(HttpMethod.POST.name(), requestBody)
                .headers(headerBuild)
                .build();

        Response response = null;

        try {
            response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                ResponseBody body = response.body();

                if (body != null) {
                    String jsonTxt = body.string();
                    body.close();

                    log.info("request: {}", jsonTxt);

                    JsonObject resultJsonObj = new Gson().fromJson(jsonTxt, JsonObject.class);
                    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    return objectMapper.readValue(resultJsonObj.toString(), resClass);
                }
            }

        } catch (Exception e) {
            log.error("API 호출 오류 : {}, url : {}, params: {}", e.getMessage(), url, null, e);

        } finally {
            stopWatch.stop();
            loggingRunTime(url, "POST", paramCondition, response, addHeaders, stopWatch.getTotalTimeSeconds());
        }
        return null;
    }

    public <T> T callPostParamApi(String url, RequestBody multipartBody, Map<String, String> addHeaders, Class<T> resClass) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T res = null;
        Response response = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .method(HttpMethod.POST.name(), multipartBody)
                    .headers(Headers.of(addHeaders))
                    .build();

            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ResponseBody body = response.body();

                if (body != null) {
                    String jsonTxt = body.string();
                    body.close();

                    log.info("request: {}", jsonTxt);

                    JsonObject resultJsonObj = new Gson().fromJson(jsonTxt, JsonObject.class);
                    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    res = objectMapper.readValue(resultJsonObj.toString(), resClass);
                }
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("\n= RES : ").append(StringTools.getStrToLimitNum(JsonUtils.writeValueAsString(response.body()), 1000));
                log.info(builder.toString());
            }
        } catch (Exception e) {
            log.error("API 호출 오류 : {}, url : {}, params: {}", e.getMessage(), url, null, e);
        } finally {
            stopWatch.stop();
            loggingRunTime(url, "POST", multipartBody, response, addHeaders, stopWatch.getTotalTimeSeconds());
        }
        return res;
    }

    public <T> T callPostParamApi(String url, Map<String, String> addHeaders, Class<T> resClass, String... params) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        T res = null;
        Response response = null;
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody requestBody = RequestBody.create(mediaType, StringTools.joinStrings("&", params));
        try {

            Request request = new Request.Builder()
                    .url(url)
                    .headers(Headers.of(addHeaders))
                    .method(HttpMethod.POST.name(), requestBody)
                    .build();

            response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                ResponseBody body = response.body();

                if (body != null) {
                    String jsonTxt = body.string();
                    body.close();

                    log.info("request: {}", jsonTxt);

                    JsonObject resultJsonObj = new Gson().fromJson(jsonTxt, JsonObject.class);
                    ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    res = objectMapper.readValue(resultJsonObj.toString(), resClass);
                }
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("\n= RES : ").append(StringTools.getStrToLimitNum(JsonUtils.writeValueAsString(response.body()), 1000));
                log.info(builder.toString());
            }
        } catch (Exception e) {
            log.error("API 호출 오류 : {}, url : {}, params: {}", e.getMessage(), url, null, e);
        } finally {
            stopWatch.stop();
            loggingRunTime(url, "POST", requestBody, response, addHeaders, stopWatch.getTotalTimeSeconds());
        }
        return res;
    }


    private void loggingRunTime(String url, String methodType, Object reqParams, Object response, Object addHeaders, double totalTimeSeconds) {
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
                if (reqParams instanceof MultipartBody) {
                    builder.append("\n= REQ : ").append(StringTools.getStrToLimitNum(bodyToString((MultipartBody)reqParams), 1000));
                } else {
                    builder.append("\n= REQ : ").append(StringTools.getStrToLimitNum(JsonUtils.writeValueAsString(reqParams), 1000));
                }
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

    private OkHttpClient getUnsafeOkHttpClient(String url) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            SSLContext sslContext = null;
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    //hostname = "bsmsapidev.syrup.co.kr";
                    System.out.println("hostnameVerifier =============");
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            //throw new RuntimeException(e);
            log.warn("Exception while configuring IgnoreSslCertificate" + e, e);
        }
        return null;
    }

    private String bodyToString(RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null) {
                copy.writeTo(buffer);
            } else {
                return "";
            }
            return buffer.readUtf8();

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return "bodyToString error";
        }
    }
}