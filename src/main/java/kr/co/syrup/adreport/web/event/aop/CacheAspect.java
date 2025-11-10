package kr.co.syrup.adreport.web.event.aop;

import kr.co.syrup.adreport.framework.config.properties.ProfileProperties;
import kr.co.syrup.adreport.framework.utils.PredicateUtils;
import kr.co.syrup.adreport.web.event.entity.CommonSettingsEntity;
import kr.co.syrup.adreport.web.event.service.ArEventService;
import kr.co.syrup.adreport.web.event.service.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * 캐싱 AOP
 */
@Slf4j
@Order(1)
@Component
@Aspect
public class CacheAspect {

    private static Long wasVersion = 0L;

    private static Instant lastReadTime = Instant.now();

    private static int cacheTime = 60;

    @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
    public void filterPattern() {}

    @Pointcut("execution(* *(..))")
    public void methodPointcut() {}

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ArEventService arEventService;

    @PostConstruct
    public void init() {
        if (ProfileProperties.isAlp() || ProfileProperties.isProd()) {
            cacheTime = findCacheSecond();
        }
    }

    /**
     * 캐싱 시간 정보 DB 가져오기
     * @return
     */
    public Integer findCacheSecond() {
        if (ProfileProperties.isAlp() || ProfileProperties.isProd()) {
            CommonSettingsEntity commonSettings = arEventService.findCommonSettingsBySettingKey("cache_limit_second");
            if (PredicateUtils.isNotNull(commonSettings.getId())) {
                return Integer.parseInt(commonSettings.getValue());
            }
        }
        return 60;
    }

    @Before("filterPattern()")
    public void cacheAspectMethod(JoinPoint jp) throws Throwable {
        //개발, 로컬은 캐싱안함
        if (ProfileProperties.isLocal() || ProfileProperties.isDev()) {
            cacheService.clearAllCache();
        } else {
            Instant currentTime = Instant.now();
            long betweenSecond = Duration.between(lastReadTime, currentTime).getSeconds();
            if (betweenSecond > cacheTime) {
                Long sodarVersion = cacheService.findCacheableSodarVersion();
                if (PredicateUtils.isNotNull(sodarVersion)) {
                    if (sodarVersion > wasVersion) {
                        cacheService.clearAllCache();
                        wasVersion = sodarVersion;
                    }
                    lastReadTime = currentTime;
                }
            }
        }
    }
}
