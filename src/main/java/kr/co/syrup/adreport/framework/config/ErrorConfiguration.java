//package kr.co.syrup.adreport.framework.config;
//
////import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer; // ConfigurableServletWebServerFactory 로 변경
//import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
//import org.springframework.boot.web.server.ErrorPage;
//import org.springframework.boot.web.server.WebServerFactoryCustomizer;
//import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//
/////**
//// * Created by jino on 2017. 2. 7..
//// */
////@Configuration
//public class ErrorConfiguration implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
//    @Override
//    public void customize(ConfigurableServletWebServerFactory factory) {
//        factory.addErrorPages(
//                new ErrorPage(HttpStatus.NOT_FOUND, "/errors/pageNotFound"),
//                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/errors/default-exception"),
//                new ErrorPage("/errors/default-exception"));
//    }
//}
