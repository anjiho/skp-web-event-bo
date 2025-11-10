package kr.co.syrup.adreport.framework.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.BooleanTypeHandler;
import org.apache.ibatis.type.DateTypeHandler;
import org.apache.ibatis.type.TypeHandler;
import org.aspectj.lang.annotation.Aspect;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Aspect
@Slf4j
@Configuration
@MapperScan(
        basePackages = {"kr.co.syrup.adreport"},
        annotationClass = Mapper.class,
        sqlSessionFactoryRef = "sqlSessionFactory"
)
public class MybatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();

        sessionFactory.setConfiguration(new MybatisConfiguration());
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath:mappers/*.xml")
        );
        sessionFactory.setTypeAliasesPackage("kr.co.syrup.adreport.web.event.mybatis.vo");
        sessionFactory.setVfs(SpringBootVFS.class);
        sessionFactory.setTypeHandlers(new TypeHandler[] {
                new DateTypeHandler(),
                new BooleanTypeHandler()
        });

        return sessionFactory.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
