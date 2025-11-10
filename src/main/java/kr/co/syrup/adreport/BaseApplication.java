package kr.co.syrup.adreport;

import kr.co.syrup.adreport.framework.utils.StringTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
// SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
//import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory; // TomcatServletWebServerFactory 로 변경
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@EnableCaching
@EnableWebMvc
@EntityScan("kr.co.syrup.adreport")
@EnableJpaRepositories("kr.co.syrup.adreport")
@SpringBootApplication
public class BaseApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        setSpringProfileActive();
        //setAes256KeyActive();
        SpringApplication.run(BaseApplication.class, args);
    }

    //Tomcat large file upload connection reset
    //http://www.mkyong.com/spring/spring-file-upload-and-connection-reset-issue/
    //https://www.python2.net/questions-1058795.htm
    @Bean
    // SS-19919 Web AR / ADREPORT 보안진단결과 취약점 수정 (2022년) - springboot 버전업
    public TomcatServletWebServerFactory tomcatEmbedded() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        tomcat.addConnectorCustomizers((TomcatConnectorCustomizer) connector -> {
            if ((connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?>)) {
                //-1 means unlimited
                ((AbstractHttp11Protocol<?>) connector.getProtocolHandler()).setMaxSwallowSize(-1);
            }
        });

//
//        tomcat.addErrorPages(
//                new ErrorPage(HttpStatus.NOT_FOUND, "/error/pageNotFound"),
//                new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/default-exception"),
//                new ErrorPage("/errors/default-exception")
//        );
        return tomcat;
    }

    public static void setSpringProfileActive() {
        if (StringTools.isNull2(System.getProperty("spring.profiles.active"))) {
            log.info("spring.profiles.active is not defined. Set to the local.");
            System.setProperty("spring.profiles.active", "local");
        }
        log.info("current spring.profiles.active : {}", System.getProperty("spring.profiles.active"));
    }

//    public static void setAes256KeyActive() {
//        String profile = System.getProperty("spring.profiles.active");
//        if ("local".equals(profile)) {
//            System.setProperty("aeskey.value", "534b504c414e45544145535554494c53");
//        } else {
//            System.setProperty("aeskey.value", "12345");
//        }
//        log.info("aesKey : {}", System.getProperty("aeskey.value"));
//    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        BaseApplication.setSpringProfileActive();
        //BaseApplication.setAes256KeyActive();
        setRegisterErrorPageFilter(false);
        return application.sources(BaseApplication.class);
    }



}
