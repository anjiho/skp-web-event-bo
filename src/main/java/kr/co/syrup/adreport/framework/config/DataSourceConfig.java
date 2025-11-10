package kr.co.syrup.adreport.framework.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
public class DataSourceConfig implements TransactionManagementConfigurer {
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.hikari.jdbcUrl}")
    private String jdbcUrl;

    @Value("${spring.datasource.hikari.poolName}")
    private String poolName;

    @Value("${spring.datasource.hikari.connectionTimeout}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.maximumPoolSize}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimumIdle}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.idleTimeout}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.maxLifetime}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.leakDetectionThreshold}")
    private long leakDetectionThreshold;

    @Bean
    //@ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setPoolName(poolName);
        hikariConfig.setMaximumPoolSize(maximumPoolSize);
        hikariConfig.setMinimumIdle(minimumIdle);
        hikariConfig.setIdleTimeout(idleTimeout);
        hikariConfig.setMaxLifetime(maxLifetime);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setLeakDetectionThreshold(leakDetectionThreshold);

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        //DataSource dataSource = DataSourceBuilder.create().build();

        log.info(">>> datasource : {}", dataSource);

        return dataSource;
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setDataSource(dataSource());
        return transactionManager;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}
