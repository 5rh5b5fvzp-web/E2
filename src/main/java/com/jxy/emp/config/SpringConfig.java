package com.jxy.emp.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

/**
 * Spring 根容器配置：数据源、MyBatis-Plus、Mapper 扫描、声明式事务。
 * 注意：这里只扫描 Service 层，Controller 交给 SpringMVC 子容器，避免父子容器重复注册。
 */
@Configuration
@ComponentScan(basePackages = "com.jxy.emp.service")
@PropertySources({
        // 本机口令文件放在前面，优先级高于默认配置；文件不存在时忽略
        @PropertySource(value = "classpath:jdbc-local.properties", ignoreResourceNotFound = true),
        @PropertySource("classpath:jdbc.properties"),
        @PropertySource("classpath:app.properties")
})
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringConfig {

    @Bean
    public DataSource dataSource(@Value("${jdbc.driver}") String driver,
                                 @Value("${jdbc.url}") String url,
                                 @Value("${jdbc.username}") String username,
                                 @Value("${jdbc.password:}") String password) {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(5000);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        return dataSource;
    }

    /**
     * 必须使用 MyBatis-Plus 提供的 MybatisSqlSessionFactoryBean，
     * 否则 BaseMapper 的通用 CRUD 不会被注册，运行时会报 Invalid bound statement。
     */
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws IOException {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage("com.jxy.emp.entity");
        factory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/*.xml"));

        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(Slf4jImpl.class);
        factory.setConfiguration(configuration);

        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pagination = new PaginationInnerInterceptor(DbType.MYSQL);
        pagination.setMaxLimit(100L);
        interceptor.addInnerInterceptor(pagination);
        factory.setPlugins(interceptor);

        return factory;
    }

    /** 扫描 Mapper 接口，生成动态代理对象 */
    @Bean
    public static MapperScannerConfigurer mapperScannerConfigurer() {
        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        configurer.setBasePackage("com.jxy.emp.mapper");
        return configurer;
    }

    /** 声明式事务管理器 */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
