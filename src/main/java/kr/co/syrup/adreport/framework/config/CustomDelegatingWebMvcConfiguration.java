package kr.co.syrup.adreport.framework.config;

import kr.co.syrup.adreport.framework.resolver.CustomExceptionHandlerExceptionResolver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jino on 2017. 1. 31..
 */
@Configuration
public class CustomDelegatingWebMvcConfiguration extends DelegatingWebMvcConfiguration implements ApplicationContextAware {

    private static final boolean jackson2Present =
            ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", CustomDelegatingWebMvcConfiguration.class.getClassLoader()) &&
                    ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", CustomDelegatingWebMvcConfiguration.class.getClassLoader());

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }

    @Bean
    @Override
    // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
    public HandlerExceptionResolver handlerExceptionResolver(@Qualifier("mvcContentNegotiationManager") ContentNegotiationManager contentNegotiationManager) {
        List<HandlerExceptionResolver> exceptionResolvers = new ArrayList<HandlerExceptionResolver>();
        configureHandlerExceptionResolvers(exceptionResolvers);

        if (exceptionResolvers.isEmpty()) {
            addCustomDefaultHandlerExceptionResolvers(exceptionResolvers);
        }

        HandlerExceptionResolverComposite composite = new HandlerExceptionResolverComposite();
        composite.setOrder(0);
        composite.setExceptionResolvers(exceptionResolvers);
        return composite;
    }

    private void addCustomDefaultHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        CustomExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new CustomExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setApplicationContext(this.applicationContext);
        exceptionHandlerExceptionResolver.setContentNegotiationManager(mvcContentNegotiationManager());
        exceptionHandlerExceptionResolver.setMessageConverters(getMessageConverters());
        if (jackson2Present) {
            List<ResponseBodyAdvice<?>> interceptors = new ArrayList<ResponseBodyAdvice<?>>();
            interceptors.add(new JsonViewResponseBodyAdvice());
            exceptionHandlerExceptionResolver.setResponseBodyAdvice(interceptors);
        }
        exceptionHandlerExceptionResolver.afterPropertiesSet();

        exceptionResolvers.add(exceptionHandlerExceptionResolver);
        exceptionResolvers.add(new ResponseStatusExceptionResolver());
        exceptionResolvers.add(new DefaultHandlerExceptionResolver());
    }

}
