package kr.co.syrup.adreport.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class RequestUtils {
    public static final String SESSION_PREFIX = "session_";

    /**
     * ajax 요청여부
     *
     * @param request HttpServletRequest
     * @return ajax 요청 여부
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestHeader = request.getHeader("x-requested-with");
        log.debug("requestHeader : {}", requestHeader);
        if ("XMLHttpRequest".equals(requestHeader)) {
            return true;
        }
        return false;
    }

    public static boolean isDataRequest(HttpServletRequest request) {
        final String contentType = request.getContentType();
        log.debug("contentType : {}", contentType);
        final String accept = request.getHeader("accept");
        log.debug("accept : {}", accept);
        String mediaType = contentType;
        if (!StringUtils.hasText(mediaType)) {
            mediaType = accept;
        }
        if (StringUtils.endsWithIgnoreCase(mediaType, "json")) {
            return true;
        }
        if (StringUtils.endsWithIgnoreCase(mediaType, "xml")) {
            return true;
        }
        return false;
    }

    public static String getHostName(HttpServletRequest request) {
        String sHostName = "";
        String sRequestURL = request.getRequestURL().toString();
        String sRequestURI = request.getRequestURI();
        try {
            sHostName = sRequestURL.substring(0, sRequestURL.indexOf(sRequestURI));
            int protNextIdx = sHostName.indexOf("://");
            sHostName = sHostName.substring(3 + protNextIdx);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sHostName;
    }


    public static String getProtocolAndHostName(HttpServletRequest request) {
        String sProtocolAndHostName = "";
        String sRequestURL = request.getRequestURL().toString();
        String sRequestURI = request.getRequestURI();
        try {
            sProtocolAndHostName = sRequestURL.substring(0, sRequestURL.indexOf(sRequestURI));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return sProtocolAndHostName;
    }


    /**
     * ContextPath를 제외한 RequestURI를 구한다.
     *
     * @param request HttpServletRequest
     * @return URI
     */
    public static String getRequestURIExcludeContextPath(
            HttpServletRequest request) {
        final String uri = request.getRequestURI();
        int pathLength = request.getContextPath() == null ? 0 : request
                .getContextPath().length();
        return uri.substring(pathLength);
    }

    /**
     * request의 Parameter정보 Map 정보를 생성한다.
     *
     * @param <T>
     * @param request               HttpServletRequest
     * @param isIncludeSessionValue 세션 정보 포함 할지 여부
     * @return
     */
    public static <T> Map<String, Object> getParameterMap(
            HttpServletRequest request, Class<T> type,
            boolean isIncludeSessionValue) {
        return getParameterMap(request, SESSION_PREFIX, type,
                isIncludeSessionValue);
    }

    /**
     * request의 Parameter정보 Map 정보를 생성한다.
     *
     * @param <T>
     * @param request
     * @param sessionPrefix
     * @param isIncludeSessionValue 세션 정보 포함 할지 여부
     * @return
     */
    public static <T> Map<String, Object> getParameterMap(
            HttpServletRequest request, String sessionPrefix, Class<T> type,
            boolean isIncludeSessionValue) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(getParameterMap(request));
        if (isIncludeSessionValue) {
            mapIncludeMemberSession(map, sessionPrefix);
        }
        return map;
    }

    /**
     * request의 Parameter정보 Map 정보를 생성한다.
     *
     * @param request 세션 정보 포함 할지 여부
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        Enumeration<String> e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String key = e.nextElement();
            String[] params = request.getParameterValues(key);
            if (params.length <= 1)
                map.put(key, request.getParameter(key));
            else {
                map.put(key, params);
            }
        }
        return map;
    }

    /**
     * Session정보의 Map을 생성한다.
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getSessionMap(HttpServletRequest request) {
        return getSessionMap(request, SESSION_PREFIX);
    }

    /**
     * Session정보의 Map을 생성한다.
     *
     * @return
     */
    public static Map<String, Object> getSessionMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        mapIncludeMemberSession(map, SESSION_PREFIX);
        return map;
    }

    /**
     * Session정보의 Map을 생성한다.
     *
     * @param sessionPrefix
     * @return
     */
    public static Map<String, Object> getSessionMap(String sessionPrefix) {
        Map<String, Object> map = new HashMap<String, Object>();
        mapIncludeMemberSession(map, sessionPrefix);
        return map;
    }

    /**
     * Session정보의 Map을 생성한다.
     *
     * @param <T>
     * @param request
     * @param sessionPrefix
     * @return
     */
    public static <T> Map<String, Object> getSessionMap(
            HttpServletRequest request, String sessionPrefix) {
        Map<String, Object> map = new HashMap<String, Object>();
        mapIncludeMemberSession(map, sessionPrefix);
        return map;
    }

    /**
     * 세션 정보를 포함 시킨다.
     *
     * @param <T>
     * @param map
     */
    private static <T> void mapIncludeSession(Map<String, Object> map,
                                              String sessionPrefix, T user) {
        BeanWrapper bw = new BeanWrapperImpl(user);
        PropertyDescriptor[] pds = bw.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            String name = pd.getName();
            Object value;
            try {
                value = bw.getPropertyValue(name);
            } catch (NotReadablePropertyException e) {
                log.warn("{}에서 '{}' 속성은 가져 올 수 없습니다.", user.getClass(), name);
                continue;
            }
            String fullName = sessionPrefix + name;
            if (map.containsKey(fullName)) {
                log.warn("현재 세션 속성명과 동일한 속성이 포함되어 있습니다. 동일 속성명(" + fullName
                        + ")");
            }
            map.put(fullName, value);
        }
    }

    /**
     * 지정 세션 정보를 가져온다.
     *
     * @param request
     * @param attr
     * @return
     */
    public static Object getSession(HttpServletRequest request, String attr) {
        Object obj = WebUtils.getSessionAttribute(request, attr);
        return obj;
    }

    /**
     * Member 정보르 세션에 포함 시킨다.
     *
     * @param map
     */
    public static void mapIncludeMemberSession(Map<String, Object> map, String sessionPrefix) {
        /*
		Authentication auth = AuthorityUtils.getAuthentication();
		if (auth == null) {
			return;
		}
		Object principal = auth.getPrincipal();
		if (!(principal instanceof IStoreUser)) {
			return;
		}
		IStoreUser user = (IStoreUser) principal;
		Member member = user.getMember();
		mapIncludeSession(map, sessionPrefix, member);
		*/
    }

    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String ip = request.getHeader("x-forwarded-for");
        if (hasNotIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (hasNotIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (hasNotIp(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }


    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || userAgent.length() <= 0) {
            userAgent = request.getHeader("user-agent");
        }

        return userAgent;
    }

    public static String getAccept(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String accept = request.getHeader("Accept");

        if (accept == null || accept.length() <= 0) {
            accept = request.getHeader("accept");
        }

        return accept;
    }

    private static boolean hasNotIp(String ip) {
        return ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip);
    }

    public static HttpServletRequest getRequest() {
        try {
            RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
            return ((ServletRequestAttributes) attrs).getRequest();
        } catch (Exception e) {
            log.debug("", e);
        }
        return null;
    }
}
