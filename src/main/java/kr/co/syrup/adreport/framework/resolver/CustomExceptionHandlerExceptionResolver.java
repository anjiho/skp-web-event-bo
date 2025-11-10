package kr.co.syrup.adreport.framework.resolver;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jino on 2017. 1. 31..
 */
public class CustomExceptionHandlerExceptionResolver extends ExceptionHandlerExceptionResolver {
    public static final String ORIGINAL_OCCURRED_EXCEPTION_HANDLER_ATTR =
            "ORIGINAL_OCCURRED_EXCEPTION_HANDLER";

    @Override
    protected ModelAndView doResolveHandlerMethodException(HttpServletRequest request,
                                                           HttpServletResponse response, HandlerMethod handlerMethod, Exception exception) {
        request.setAttribute(ORIGINAL_OCCURRED_EXCEPTION_HANDLER_ATTR, handlerMethod);
        return super.doResolveHandlerMethodException(request, response, handlerMethod, exception);
    }
}
