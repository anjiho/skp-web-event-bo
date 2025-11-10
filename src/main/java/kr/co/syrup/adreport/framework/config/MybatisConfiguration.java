package kr.co.syrup.adreport.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;

@Slf4j
public class MybatisConfiguration extends Configuration {

    public MybatisConfiguration() {
        this.setCacheEnabled(false);
        this.setUseGeneratedKeys(true);
        this.setDefaultExecutorType(ExecutorType.REUSE);
        this.setAggressiveLazyLoading(false);
        this.setLazyLoadingEnabled(false);
        //this.setLazyLoadTriggerMethods(null);
        this.setMapUnderscoreToCamelCase(true);
        this.setCallSettersOnNulls(false);
    }
}
