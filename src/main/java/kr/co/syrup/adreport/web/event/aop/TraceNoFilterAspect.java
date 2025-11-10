package kr.co.syrup.adreport.web.event.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kr.co.syrup.adreport.framework.utils.GsonUtils;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.dto.request.EventSaveDto;
import kr.co.syrup.adreport.web.event.dto.response.ApiResultObjectDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 리턴해줄 traceNo 값 주입 AOP
 */
@Slf4j
@Component
@Aspect
public class TraceNoFilterAspect {

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("@within(org.springframework.stereotype.Controller)")
    public void controller() {}

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restController() {}

    @Pointcut("@within(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {}

    //@Pointcut("@annotation(kr.co.syrup.adreport.web.event.define.TraceNoFilter)")
    @Pointcut("@annotation(kr.co.syrup.adreport.framework.common.annotation.TraceNoFilter)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Around("(controller() || restController()) && requestMapping() && filterPattern() && methodPointcut()")
    public Object afterControllerMethod(ProceedingJoinPoint pjp) throws Throwable {

        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest(); // request 정보를 가져온다.

        Map<String, Object> params = new HashMap<>();

        String requestBodyStr = "";
        String methodName = "";
        try {
            //requestBody json 가져오기
            MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
            methodName = methodSignature.getMethod().getName();

            Annotation[][] annotationMatrix = methodSignature.getMethod().getParameterAnnotations();
            int index = -1;
            for (Annotation[] annotations : annotationMatrix) {
                index++;
                for (Annotation annotation : annotations) {
//                     if (!(annotation instanceof RequestBody)) continue;
                    //annotation 이 @RequestBody, @RequestPart 일때
                    if ((annotation instanceof  RequestBody) || (annotation instanceof RequestPart)) {

                        Object requestBody = pjp.getArgs()[index];

                        if ( (PredicateUtils.isNotNull(requestBody)) ) {

                            if (requestBody.toString().contains("traceNo")) {
                                //@RequestPart 이고 특정 메서드 이름 예외처리
                                if ("saveEvent".equals(methodName) || "updateEvent".equals(methodName) || "saveSurveyGoSodarData".equals(methodName) || "updateSurveyGoSodarData".equals(methodName)
                                    || "savePhotoEvent".equals(methodName) || "updatePhotoEvent".equals(methodName) || "saveStampEvent".equals(methodName) || "updateStampEvent".equals(methodName)
                                ) {
                                    requestBodyStr = requestBody.toString();

                                } else {

                                    Gson gson = new Gson();
                                    requestBodyStr = gson.toJson(requestBody);
                                }
                            }

                        }
                    } else {
                        //request Params 가져오기
                        params.put("params", getParams(request));
                    }
                }
            }
//            //request Params 가져오기
//            params.put("params", getParams(request));
        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        }
//        log.info("requestBodyStr : {}", requestBodyStr);
//        log.info("params : {}", params);

        Object entity = pjp.proceed();

        if(entity instanceof ResponseEntity){
            ResponseEntity resEntity = (ResponseEntity)entity;
            Object object = resEntity.getBody();
            ApiResultObjectDto dto = (ApiResultObjectDto)object;

            String traceNo = "";

            //reqeustBody(json body)
            if (StringUtils.isNotEmpty(requestBodyStr)) {
                if (requestBodyStr.contains("traceNo")) {

                    if ("saveEvent".equals(methodName) || "updateEvent".equals(methodName) || "saveSurveyGoSodarData".equals(methodName) || "updateSurveyGoSodarData".equals(methodName)
                            || "savePhotoEvent".equals(methodName) || "updatePhotoEvent".equals(methodName)
                    ) {
                        EventSaveDto eventSaveDto = objectMapper.readValue(requestBodyStr, EventSaveDto.class);
                        traceNo = eventSaveDto.getTraceNo();

                    } else {

                        traceNo = GsonUtils.parseStringJsonStr(requestBodyStr, "traceNo");
                    }
                }
            }

            //reqeust param
            if ( PredicateUtils.isNotNull(params.get("params")) ) {
                JSONObject jsonObject = getParams(request);
                if (jsonObject.toString().contains("traceNo")) {
                    traceNo = jsonObject.get("traceNo").toString();
                }
            }

            //ApiResultObjectDto setTraceNo
            if (StringUtils.isNotEmpty(traceNo)) {
                dto.setTraceNo(traceNo);
            } else if (StringUtils.isEmpty(dto.getTraceNo())) {
                //request traceNo가 없을때 traceNo 생성
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                dto.setTraceNo(dateFormat.format(calendar.getTime()) + RandomStringUtils.randomNumeric(3));
            }

            //log.info("methodName :: {} ", methodName);
            //log.info("traceNo :: {} ", dto.getTraceNo());

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add("Content-Type", "application/json; charset=UTF-8");
            return new ResponseEntity(dto, responseHeaders, HttpStatus.OK);
        }
        return null;
    }

    /**
     * request 에 담긴 정보를 JSONObject 형태로 반환한다.
     * @param request
     * @return
     */
    private static JSONObject getParams(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }

}