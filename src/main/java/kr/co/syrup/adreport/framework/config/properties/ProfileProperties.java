package kr.co.syrup.adreport.framework.config.properties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@ConfigurationProperties(prefix = ProfileProperties.PREFIX)
public class ProfileProperties {

    public static final String PREFIX = "spring.profiles";

    @Value("${spring.profiles}")
    private String active;

    private static String currentActive;

    @PostConstruct
    private void postConstruct() {
        ProfileProperties.currentActive = active;
        log.info("Current active Profile : {}", active);
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public static String getCurrentActive() {
        return currentActive;
    }

    public static boolean isNotProd() {
        if (!"prod".equals(currentActive)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isProd() {
        if ("prod".equals(currentActive)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAlp() {
        if ("alp".equals(currentActive)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isDev() {
        if ("dev".equals(currentActive)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isLocal() {
        if ("local".equals(currentActive)) {
            return true;
        } else {
            return false;
        }
    }
}
