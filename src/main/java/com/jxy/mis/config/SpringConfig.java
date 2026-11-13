package com.jxy.mis.config;

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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Spring 根容器：数据源、MyBatis-Plus、Mapper 扫描、声明式事务。
 *
 * <p>父子容器分工：这里只扫 Service 层；Controller 交给 SpringMVC 子容器，
 * 避免同一个 Bean 被两个容器各创建一份。
 */
@Configuration
@ComponentScan(basePackages = "com.jxy.mis.service")
@PropertySources({
        // 本机口令文件优先；文件不存在时自动忽略
        @PropertySource(value = "classpath:jdbc-local.properties", ignoreResourceNotFound = true),
        @PropertySource("classpath:jdbc.properties")
})
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringConfig {

    /** Druid 连接池：连接四要素来自配置文件，不在 Java 代码里硬编码 */
    @Bean
    public DataSource dataSource(@Value("${jdbc.driver}") String driver,
                                 @Value("${jdbc.url}") String url,
                                 @Value("${jdbc.username}") String username,
                                 @Value("${jdbc.password:${DB_PASSWORD:}}") String password) {
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
     * 必须使用 MyBatis-Plus 的 MybatisSqlSessionFactoryBean：
     * 用原生 SqlSessionFactoryBean 时 BaseMapper 的通用 CRUD 不会注册，
     * 运行时会报 Invalid bound statement。
     */
    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTypeAliasesPackage("com.jxy.mis.entity");

        MybatisConfiguration configuration = new MybatisConfiguration();
        // 数据库下划线字段自动映射到 Java 驼峰属性
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setLogImpl(Slf4jImpl.class);
        factory.setConfiguration(configuration);

        // 分页插件必须用 Java API 注册
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
        configurer.setBasePackage("com.jxy.mis.mapper");
        return configurer;
    }

    /** 声明式事务管理器 */
    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
