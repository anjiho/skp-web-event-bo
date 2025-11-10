package kr.co.syrup.adreport.framework.config;

import kr.co.syrup.adreport.framework.config.properties.PermissionProperties;
import kr.co.syrup.adreport.framework.converter.CustomObjectMapper;
import kr.co.syrup.adreport.framework.converter.VoidHttpMessageConverter;
import kr.co.syrup.adreport.framework.handlers.RequiredIpPermissionInterceptor;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
// SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by jino on 2017. 1. 19..
 */
@Slf4j
@Configuration
@EnableAsync
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, SecurityAutoConfiguration.class
})
@ComponentScan(basePackages = "kr.co.syrup")
public class ApplicationConfig implements WebMvcConfigurer, WebMvcRegistrations {
    public static final long    MAX_UPLOAD_SIZE = 50 * 1024 * 1024;
    public static final int     DEFAULT_CONNECT_TIME_OUT = 30 * 1000; // 30sec
    public static final String  DEFAULT_ACCEPTENCODING = "gzip, deflate";

    @Bean
    public RequiredIpPermissionInterceptor requiredIpPermissionInterceptor() {
        return new RequiredIpPermissionInterceptor();
    }

    @Bean("customKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new CustomKeyGenerator();
    }

    @Autowired
    private CustomObjectMapper objectMapper;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {

    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {

    }

    @Autowired
    PermissionProperties permissionProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new ThemeInterceptor()).addPathPatterns("/**").excludePathPatterns("/admin/**");
//        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns("/secure/*");

        registry.addInterceptor(requiredIpPermissionInterceptor()).addPathPatterns("/**");
        log.info("Interceptors added!");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/robots.txt").addResourceLocations("/robots.txt");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:META-INF/resources/webjars/");
        registry.setOrder(-2);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.enableContentNegotiation(new MappingJackson2JsonView(objectMapper));
        registry.jsp().prefix("/WEB-INF/jsp/").suffix(".jsp");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {

    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringHttpMessageConverter);
        converters.add(new SourceHttpMessageConverter<>());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
        converters.add(new VoidHttpMessageConverter());
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {

    }

    @Override
    public Validator getValidator() {
        return new LocalValidatorFactoryBean();
    }

    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        return null;
    }

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return null;
    }

    @Override
    public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return null;
    }

    @Override
    public ExceptionHandlerExceptionResolver getExceptionHandlerExceptionResolver() {
        return null;
    }

}
