package kr.co.syrup.adreport.web.event.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.SecurityUtils;
import kr.co.syrup.adreport.web.event.define.StringDefine;
import kr.co.syrup.adreport.web.event.dto.request.WinningSearchReqDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 컨트롤러에서 넘어온 JSON 값 암호화 AOP
 */

// controller annotation 선언 예시 @EncryptDataFilter("test,test2,test3|{value1},test5|{value1/value2}")
// request 예시
//{
//   "test1":
//      {
//        "test":"1234"
//       },
//   "test2":"2222",
//   "test3":
//    [
//        {
//            "value1":"1111",
//            "value2":"2222"
//        },
//        {
//            "value1":"33",
//            "value2":"44"
//        }
//    ],
//    "test4": {
//        "test5":
//        [
//            {
//            "value1":"1111",
//            "value2":"2222"
//            },
//            {
//            "value1":"33",
//            "value2":"44"
//            }
//        ]
//    }
//}

@Slf4j
@Component
@Aspect
public class EncryptDataFilterAspect {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AES256Utils aes256Utils;

    @Value("${encryption.use.yn}")
    private String encryptionUseYn;

    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.EncryptDataFilter)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("filterPattern() && methodPointcut()")
    public Object afterControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        EncryptDataFilter filter = methodSignature.getMethod().getAnnotation(EncryptDataFilter.class);
        JsonObject jsonObject = new JsonObject();

        Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();
        int index = -1;
        for (Annotation[] annotations : annotationMatrix) {
            index++;
            for (Annotation annotation : annotations) {

                if ((annotation instanceof RequestBody) || (annotation instanceof RequestPart)) {

                    Object requestBody = pjp.getArgs()[index];

                    if ((PredicateUtils.isNotNull(requestBody))) {

                        if (PredicateUtils.isEqualsStr(encryptionUseYn, StringDefine.Y.name())) {

                            String[] patterns = filter.values();
                            if (patterns.length == 0) {
                                patterns = new String[]{filter.value()};

                                String requestBodyPojoJson = getPrettyJsonString(requestBody);;

                                String[] patternsSplit = StringUtils.split(patterns[0], ",");
                                for (String pattern : patternsSplit) {
                                    log.info("pattern :: " + pattern);

                                    List<String>sensitiveKeys = new ArrayList<>();  // Json Array 안에 암호화 할 키 초기화 선언

                                    if (pattern.contains("{") && pattern.contains("}")) {   // Json Array 암호화 패턴 확인 > {} 존재하면 배열안을 암호화
                                        String[] splitPattern = StringUtils.split(pattern, "|");    // ex > test|{test1/test2/..} 패턴을 | 기준으로 자른다
                                        pattern = splitPattern[0];  // Json Array Key 값 (ex > test)
                                        Object object = splitPattern[1];    // Json Array Value 값 (ex > {test1/test2/...})
                                        if (object != null) {
                                            object = object.toString().substring(1);    // ex > {test1/test2/...} 앞 문자열 제거
                                            object = object.toString().substring(0, object.toString().length() - 1);    // ex > {test1/test2/...} 뒤 문자열 제거

                                            String[] splitArrayKeys = StringUtils.split(object.toString(), "/"); // ex > {test1/test2/...} "/" 기준으로 자른다
                                            for (String key : splitArrayKeys) { // ex > test1,test2,... 값을 배열로 만든다
                                                sensitiveKeys.add(key);
                                            }
                                        }
                                    }

                                    if (requestBody.toString().contains(pattern)) {
                                        Gson gson = new Gson();
                                        //@RequestBody 클래스타입이 String 일때
                                        if (requestBody instanceof String) {
                                            JsonElement element = gson.fromJson(pjp.getArgs()[index].toString(), JsonElement.class);
                                            jsonObject = element.getAsJsonObject();

                                            encryptJsonTree(jsonObject, pattern, sensitiveKeys);

                                            pjp.getArgs()[index] = jsonObject.toString();
                                        } else {
                                        //@RequestBody 클래스타입이 class 일때
                                            JsonElement element = gson.fromJson(requestBodyPojoJson, JsonElement.class);
                                            jsonObject = element.getAsJsonObject();

                                            encryptJsonTree(jsonObject, pattern, sensitiveKeys);

                                            requestBodyPojoJson = jsonObject.toString();
                                        }
                                    }
                                }
                                if (requestBody instanceof String) {
                                    return pjp.proceed(pjp.getArgs());
                                } else {
                                    // json string > pojo class 변환 작업
                                    Class<?>[] parameterTypes = methodSignature.getMethod().getParameterTypes();
                                    Class<?> targetClass = parameterTypes[index];
                                    Object pojo = objectMapper.readValue(jsonObject.toString(), targetClass);
                                    args[index] = pojo;
                                    return pjp.proceed(args);
                                }
                            }
                        } else {
                            return pjp.proceed();
                        }
                    }
                }
            }
        }
       return pjp.proceed();
    }

    private String getPrettyJsonString(Object vo) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(mapper.writeValueAsString(vo));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(je);
    }

    // json object tree 암호화
    private void encryptJsonTree(JsonObject jsonObject, String matchKey, List<String> sensitiveKeys) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (key.equals(matchKey) && value.isJsonPrimitive()) {  // 일반 key 암호화
                String encryptedValue = aes256Utils.encrypt(value.getAsString());
                jsonObject.addProperty(key, encryptedValue);
            } else if (value.isJsonObject()) {
                encryptJsonTree(value.getAsJsonObject(), matchKey, sensitiveKeys); // 다음 단계 탐색
            } else if (value.isJsonArray()) {   // 배열 key 암호화
                if (key.equals(matchKey)) {
                    JsonArray jsonArray = new JsonArray();
                    for (JsonElement element : value.getAsJsonArray()) {
                        if (element.isJsonObject()) {
                            JsonObject elementAsJsonObject = element.getAsJsonObject();

                            for (String sensitiveKey : sensitiveKeys) {
                                if (elementAsJsonObject.has(sensitiveKey)) {
                                    String originalValue = elementAsJsonObject.get(sensitiveKey).getAsString();
                                    String encryptedValue = aes256Utils.encrypt(originalValue);
                                    elementAsJsonObject.addProperty(sensitiveKey, encryptedValue);
                                }
                            }
                            jsonArray.add(elementAsJsonObject);
                        }
                    }
                    jsonObject.add(key, jsonArray);
                }
            }
        }
    }
}
