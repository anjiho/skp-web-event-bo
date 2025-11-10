package kr.co.syrup.adreport.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class CookieUtils {

    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        if (request == null) {
            log.warn("'getCookie' fail!! HttpServletRequest argument is required!");
            return null;
        }
        if (cookieName == null) {
            log.warn("'getCookie' fail!! 'cookieName' argument is required!");
            return null;
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookieName.equalsIgnoreCase(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            return "";
        }
        return cookie.getValue();
    }

    public static void setCookieValue(HttpServletResponse response, HttpServletRequest request, String cookieName, String cookieValue, String domain) {
        setCookieValue(response, request, cookieName, cookieValue, domain, null);
    }

    public static void setCookieValue(HttpServletResponse response, HttpServletRequest request, String cookieName, String cookieValue) {
        setCookieValue(response, request, cookieName, cookieValue, null, null);
    }

    public static void setCookieValue(HttpServletResponse response, HttpServletRequest request, String cookieName, String cookieValue, String domain, Integer cookieMaxAge) {
        if (response == null) {
            log.warn("'setCookieValue' fail!! HttpServletResponse argument is required!");
            return;
        }
        if (request == null) {
            log.warn("'setCookieValue' fail!! HttpServletRequest argument is required!");
            return;
        }
        if (cookieName == null) {
            log.warn("'setCookieValue' fail!! Cookie name argument is required!");
            return;
        }
        Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            addCookie(response, request, cookieName, cookieValue, domain, cookieMaxAge);
            return;
        }
        String cookiePath = request.getContextPath();
        if (!StringUtils.hasLength(cookiePath)) {
            cookiePath = "/";
        }
        cookie.setPath(cookiePath);
        log.debug("setCookie cookieName: {}, cookieValue: {}, domain: {}, cookieMaxAge: {}", cookieName, cookieValue, domain, cookieMaxAge);
        try {
            cookie.setValue(URLEncoder.encode(cookieValue, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("cookieName : {}, cookieValue : {}", cookieName, cookieValue, e);
            return;
        }
        if (StringUtils.hasText(domain)) {
            cookie.setDomain(domain);
        }
        if (cookieMaxAge != null) {
            cookie.setMaxAge(cookieMaxAge);
        }
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, HttpServletRequest request, String cookieName, String cookieValue, String domain, Integer cookieMaxAge) {
        if (response == null) {
            log.warn("'addCookie' fail!! HttpServletResponse argument is required!");
            return;
        }
        if (request == null) {
            log.warn("'addCookie' fail!! HttpServletRequest argument is required!");
            return;
        }
        if (cookieName == null) {
            log.warn("'addCookie' fail!! Cookie name argument is required!");
            return;
        }
        log.debug("addCookie cookieName: {}, cookieValue: {}, domain: {}, cookieMaxAge: {}", cookieName, cookieValue, domain, cookieMaxAge);
        Cookie cookie;
        try {
            cookie = new Cookie(cookieName, URLEncoder.encode(cookieValue, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.warn("addCookie cookieName : {}, cookieValue : {}", cookieName, cookieValue, e);
            return;
        }
        String cookiePath = request.getContextPath();
        if (!StringUtils.hasLength(cookiePath)) {
            cookiePath = "/";
        }
        cookie.setPath(cookiePath);
        if (StringUtils.hasText(domain)) {
            cookie.setDomain(domain);
        }
        if (cookieMaxAge == null) {
            cookie.setMaxAge(-1);
        } else {
            cookie.setMaxAge(cookieMaxAge);
        }
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, HttpServletRequest request, String cookieName, String cookieValue, String domain) {
        addCookie(response, request, cookieName, cookieValue, domain, 24 * 60 * 60);
    }

    public static void addCookie(HttpServletResponse response, HttpServletRequest request, String cookieName, String cookieValue) {
        addCookie(response, request, cookieName, cookieValue, null);
    }

    public static void removeCookie(HttpServletResponse response, HttpServletRequest request, String cookieName) {
        if (response == null) {
            log.warn("'removeCookie' fail!! HttpServletResponse argument is required!");
            return;
        }
        if (request == null) {
            log.warn("'removeCookie' fail!! HttpServletRequest argument is required!");
            return;
        }
        if (cookieName == null) {
            log.warn("'removeCookie' fail!! Cookie name argument is required!");
            return;
        }
        Cookie cookie = getCookie(request, cookieName);
        if (cookie == null) {
            log.warn("'removeCookie' fail!! name '{}' cookie is not found!", cookieName);
            return;
        }
        String cookiePath = request.getContextPath();
        if (!StringUtils.hasLength(cookiePath)) {
            cookiePath = "/";
        }
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        cookie.setValue(null);
        response.addCookie(cookie);
    }

}