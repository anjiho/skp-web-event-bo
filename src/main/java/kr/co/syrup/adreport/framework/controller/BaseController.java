package kr.co.syrup.adreport.framework.controller;

import kr.co.syrup.adreport.framework.common.CommonConstant;
import kr.co.syrup.adreport.framework.common.ResultCodeEnum;
import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.exception.ServiceException;
import kr.co.syrup.adreport.framework.utils.RequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class BaseController {

    private Long getLongFromHeader(HttpServletRequest request, String param) {
        String result = getFromHeader(request, param);
        if (StringUtils.isEmpty(result)) {
            log.warn("#################### no userNo in Header >>> " + RequestUtils.getRequestURIExcludeContextPath(request));
            //result = "0";
        }
        return Long.valueOf(result);
    }

    private Integer getIntegerFromHeader(HttpServletRequest request, String param) {
        String result = getFromHeader(request, param);
        if (StringUtils.isEmpty(result)) {
            log.warn("#################### no userNo in Header >>> " + RequestUtils.getRequestURIExcludeContextPath(request));
            //result = "0";
        }
        return Integer.valueOf(result);
    }

    private String getStringFromHeader(HttpServletRequest request, String param) {
        String result = getFromHeader(request, param);
        if (StringUtils.isEmpty(result)) {
            log.warn("#################### no userNo in Header >>> " + RequestUtils.getRequestURIExcludeContextPath(request));
            result = "";
        }
        return result;
    }

    private String getFromHeader(HttpServletRequest request, String param) {
        String result = request.getHeader(param);
        if (ProfileProperties.isLocal() || ProfileProperties.isDev()) {
            if (StringUtils.isEmpty(result)) {
                log.warn("########## no " + param + " in Header >>> " + RequestUtils.getRequestURIExcludeContextPath(request));
                result = request.getParameter(param);
                if (StringUtils.isEmpty(result)) {
                    log.warn("########## no " + param + " in Header and Parameter >>> " + RequestUtils.getRequestURIExcludeContextPath(request));
                }
            }
        }
        if (StringUtils.isEmpty(result)) {
            throw new ServiceException(param + " 없습니다.", ResultCodeEnum.PARAMETER_ERROR);
        }
        return result;
    }

    protected String getAuthToken(HttpServletRequest request) {
        try {
            return getFromHeader(request, CommonConstant.HEADER_PARAM_AUTH_TOKEN);
        } catch (Exception e) {
            //
        }
        return null;
    }
    protected String getOsType(HttpServletRequest request) {
        return getStringFromHeader(request, CommonConstant.HEADER_PARAM_OS_TYPE);
    }

    protected Integer getApiVersion(HttpServletRequest request) {
        return getIntegerFromHeader(request, CommonConstant.HEADER_PARAM_API_VERSION);
    }
}
