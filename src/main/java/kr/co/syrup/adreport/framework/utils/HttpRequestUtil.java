package kr.co.syrup.adreport.framework.utils;

import com.github.kevinsawicki.http.HttpRequest;
import kr.co.syrup.adreport.framework.config.ApplicationConfig;

import java.util.Locale;

public class HttpRequestUtil {
    private static boolean isInitHttpRequest = false;

    public static HttpRequest getHttpRequest(String url, String method) {
        HttpRequest httpRequest = null;

        if (!isInitHttpRequest) {
            HttpRequest.keepAlive(true);
            isInitHttpRequest = true;
        }

        if (HttpRequest.METHOD_GET.equalsIgnoreCase(method.toUpperCase(Locale.ENGLISH))) {
            httpRequest = HttpRequest.get(url);
        } else {
            httpRequest = HttpRequest.post(url);
        }

        httpRequest = httpRequest.trustAllCerts();
        httpRequest = httpRequest.trustAllHosts();
        httpRequest = httpRequest.followRedirects(true);
        httpRequest = httpRequest.acceptCharset(HttpRequest.CHARSET_UTF8);
        httpRequest = httpRequest.connectTimeout(ApplicationConfig.DEFAULT_CONNECT_TIME_OUT);

        /*
        httpRequest = httpRequest.chunk(Define.DEFAULT_CHUNK_SIZE);
        httpRequest = httpRequest.bufferSize(Define.DEFAULT_BUFFER_SIZE);
        httpRequest = httpRequest.acceptCharset(HttpRequest.CHARSET_UTF8);
        httpRequest = httpRequest.acceptGzipEncoding();
        httpRequest = httpRequest.acceptEncoding(ApplicationConfig.DEFAULT_ACCEPTENCODING);
        */

        return httpRequest;
    }

}
