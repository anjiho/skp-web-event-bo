package kr.co.syrup.adreport.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import kr.co.syrup.adreport.framework.exception.RequestParsingException;
import kr.co.syrup.adreport.framework.exception.ResponseParsingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Slf4j
@Component
public class UnirestInit {
    private static boolean isInit = false;

    private static UnirestObjectMapper unirestObjectMapper;

    @Autowired
    public void setUnirestObjectMapper(UnirestObjectMapper unirestObjectMapper) {
        UnirestInit.unirestObjectMapper = unirestObjectMapper;
    }

    @PostConstruct
    public static void init() {
        if (isInit) {
            return;
        }

        // java1.7 대응. 1.7은 true 임
        System.setProperty("jsse.enableSNIExtension", "false");

        try {

            HttpClient unsafeHttpClient = HttpClients.custom().setSSLContext(getSSLContext()).build();

            Unirest.setHttpClient(unsafeHttpClient);
            Unirest.setObjectMapper(unirestObjectMapper);
            Unirest.setDefaultHeader("accept", "application/json");
            Unirest.setDefaultHeader("Content-Type", "application/json");

        } catch (Exception e) {
            log.error("Unirest SSL Setting Error :" + e.getMessage(), e);
        }

        isInit = true;
    }

    private static SSLContext getSSLContext() throws Exception {
        final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // Intentionally left blank
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // Intentionally left blank
            }
        }};

        SSLContext context = null;
        try {
            //context = SSLContext.getInstance("TLS");
            context = SSLContext.getInstance("SSLv3");
            //context = SSLContext.getInstance("TLSv1.2");
            //context = SSLContext.getInstance("TLSv1.1");
            // tlsv1.2
            context.init(null, trustAllCerts, new SecureRandom());
        } catch (GeneralSecurityException e) {
            IOException ioException = new IOException("Security exception configuring SSL context");
            ioException.initCause(e);
            throw ioException;
        }

        return context;
    }

    @Slf4j
    @Component
    public static class UnirestObjectMapper implements ObjectMapper {
        @Autowired
        private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

        @Override
        public <T> T readValue(String value, Class<T> valueType) {
            try {
                //log.debug("value : {}", value);
                if (!StringTools.isNull2(value)) {
                    return objectMapper.readValue(value, valueType);
                } else {
                    return null;
                }
            } catch (Throwable e) {
                log.error("target Value : {}, Changing String to Object(" + this + ") was failed ...", value, e);
                throw new ResponseParsingException(e);
            }
        }

        @Override
        public String writeValue(Object value) {
            try {
                //log.debug("Object value : {}", value.toString());
                if (value != null) {
                    return objectMapper.writeValueAsString(value);
                } else {
                    return null;
                }
            } catch (JsonProcessingException e) {
                log.error("target Object : {}, Changing Object({}) to String\" was failed ...", value.toString(), value.getClass(), e);
                throw new RequestParsingException(e);
            }
        }
    }

}
