package kr.co.syrup.adreport.framework.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.syrup.adreport.framework.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class JsonUtils {
    private static ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        JsonUtils.objectMapper = objectMapper;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static final <T> T readValue(String json, Class<T> classOfT) throws Exception {
        T readValue = null;
        try {
            readValue = JsonUtils.objectMapper.readValue(json, classOfT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return readValue;
    }

    public static final String writeValueAsString(Object value) throws Exception {
        String json = null;

        try {
            json = JsonUtils.objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("JsonUtils writeValueAsString Error :" + e.getMessage(), e);
            throw e;
        }

        return json;
    }

    public static final <T> T copyValue(Object src, Class<T> classOfT) throws Exception {
        String json;
        T copyValue = null;

        try {
            json = writeValueAsString(src);
            copyValue = JsonUtils.objectMapper.readValue(json, classOfT);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }

        return copyValue;
    }

    public static final <T> T changeObject(Object value, Class<T> classOfT) throws Exception {
        return readValue(writeValueAsString(value), classOfT);
    }

    public static void main(String[] args) {
        String clientIp = "10.211.106.34 , 172.22.203.36";
        List<String> ips = Arrays.asList("172.22.203.35,172.22.203.36,172.22.203.37");
        if (ips.contains(clientIp)) {
            System.out.println("1");
        } else {
            System.out.println("2");
        }
    }

}
