package kr.co.syrup.adreport.framework.handlers;

import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.common.annotation.RequiredIpPermission;
import kr.co.syrup.adreport.framework.exception.BaseException;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.framework.utils.RequestUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import kr.co.syrup.adreport.web.event.entity.WebEventIpAccess;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
@Component
public class RequiredIpPermissionInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    ArEventService arEventService;

//    @Getter
//    @Setter
//    List ips;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;

            if (method.hasMethodAnnotation(RequiredIpPermission.class)) {
                log.debug("preHandle call......");
                String uriPath = request.getServletPath();
                log.info ("URL is {}", uriPath);

                String clientIp = RequestUtils.getClientIp(request);

                boolean isMatchIp = false;
                StringTokenizer st = new StringTokenizer(clientIp, ",");
                while (st.hasMoreTokens()) {
                    String nextStr = st.nextToken();

                    String profile = System.getProperty("spring.profiles.active");
                    if (PredicateUtils.isNull(profile)) {
                        profile = "local";
                    }
                    //로컬 개발은 무조건 통과
                    if (StringUtils.equals(profile, "local")) {
                        isMatchIp = true;
                        break;
                    }
                    List<WebEventIpAccess> webEventIpAccessList = arEventService.findWebEventIpAccessListByBuildLevelAndUrlPath(profile, uriPath);
                    log.debug("webEventIpAccessList : "+ webEventIpAccessList.toString());

                    for (WebEventIpAccess ipAccess : webEventIpAccessList) {
                        if (String.valueOf(ipAccess.getIpAddress()).indexOf("**") != -1) {
                            String[] splitIp = StringUtils.split(ipAccess.getIpAddress(), ".");
                            String twoIpStr =  StringTools.joinStrings(".", splitIp[0], splitIp[1]);

                            if (nextStr.trim().contains(twoIpStr) && StringUtils.equals(uriPath, ipAccess.getUrlPath())) {
                                isMatchIp = true;
                            }
                        } else {
                            if(ipAccess.getIpAddress().contains(nextStr.trim()) && StringUtils.equals(uriPath, ipAccess.getUrlPath())){
                                isMatchIp = true;
                            }
                        }
                    }
                }

                if (isMatchIp) {
                    log.debug("ip access : " + method.getMethod().getName() + " / " + clientIp);
                    return true;
                } else {
                    log.debug("ip access deny : " + method.getMethod().getName() + " / " + clientIp);
                    throw new BaseException(ResultCodeEnum.IP_ACCESS_DENY_ERROR.getDesc(), ResultCodeEnum.IP_ACCESS_DENY_ERROR);
                }
            }
            log.debug("preHandle call end");
        }
        return true;
    }
}


