package kr.co.syrup.adreport.framework.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.syrup.adreport.framework.utils.AES256Utils;
import kr.co.syrup.adreport.framework.utils.RequestUtils;
import kr.co.syrup.adreport.framework.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Component
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {
    private static final String lineSeparator = System.getProperty("line.separator");
    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
//            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AES256Utils aes256Utils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            filterChain.doFilter(request, response);
            stopWatch.stop();
        } finally {
            logging(request, response, stopWatch.getTotalTimeSeconds());
            response.copyBodyToResponse();
        }
    }

    protected void logging(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, double totalTimeSeconds) throws IOException {
        if (log.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder();

            sb.append(lineSeparator).append("===== Request & Response =====");
            sb.append(lineSeparator).append("> Total Run Time   : ").append(totalTimeSeconds).append("sec");
            sb.append(lineSeparator).append("> Request URL      : ").append(request.getRequestURL());
            sb.append(lineSeparator).append("> Request Method   : ").append(request.getMethod());
            sb.append(lineSeparator).append("> Request Remote Address: ").append(RequestUtils.getClientIp(request));
            sb.append(lineSeparator).append("> Request Header   : ");
            sb.append(lineSeparator).append("  # ").append("contentType : ").append(request.getContentType());
            sb.append(lineSeparator).append("  # ").append("accept : ").append(request.getHeader("accept"));
            sb.append(lineSeparator).append("> Request Parameters : ");
            Map<String, String[]> paramMap = request.getParameterMap();

            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                sb.append(lineSeparator).append("  # ");
                sb.append(entry.getKey()).append("=");
                String[] values = entry.getValue();
                for (int i = 0; i < values.length; i++) {
                    sb.append(StringTools.getStrToLimitNum(values[i], 400));
                }
            }

            if (request.getContentAsByteArray().length > 0) {
                //String checkContent = getContent(request, null, 1000);
                //boolean isContains = StringTools.isContainsAsStringList(checkContent, Arrays.asList("name", "phoneNumber"));
                //log.info("isContains ::: " + isContains);
                //sb.append(lineSeparator).append("> Request Body : ").append(!isContains ? getContent(request, null, 1000) : aes256Utils.encrypt(checkContent));
                sb.append(lineSeparator).append("> Request Body : ").append(getContent(request, null, 1000));
            }

            sb.append(lineSeparator);

            sb.append(lineSeparator).append("< Response Status : ").append(response.getStatusCode());
            sb.append(lineSeparator).append("< Response ContentType : ").append(response.getContentType());
            sb.append(lineSeparator).append("< Response Body : ").append(getContent(null, response, 1000));
            sb.append(lineSeparator).append("< Response Header set-cookie:").append(response.getHeader("set-cookie"));
            sb.append(lineSeparator).append("===== Request & Response END =====");

            log.info(sb.toString());
        }
    }

    private static String getContent(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, int strToLimitNum) {
        byte[] content = null;
        String contentType;
        String contentEncoding;

        if (request != null) {
            content = request.getContentAsByteArray();
            contentType = request.getContentType();
            contentEncoding = request.getCharacterEncoding();
        } else {
            content = response.getContentAsByteArray();
            contentType = response.getContentType();
            contentEncoding = response.getCharacterEncoding();
        }

        if (content.length > 0) {
            StringBuilder ret = new StringBuilder();
            val mediaType = MediaType.valueOf(contentType);
            val visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
            if (visible) {
                try {
                    val contentString = new String(content, contentEncoding);
                    Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> ret.append(line));
                } catch (UnsupportedEncodingException e) {
                    ret.append("content.length : ").append(content.length);
                }
            } else {
                ret.append("content.length : ").append(content.length);
            }

            return StringTools.getStrToLimitNum(ret.toString(), strToLimitNum);
        } else {
            return "";
        }
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }
}