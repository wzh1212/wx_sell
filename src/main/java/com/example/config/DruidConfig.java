package com.example.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.google.common.collect.Lists;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 对 druid 进行过滤器的配置
 * @Configuration : 用于定义配置类
 * @Bean ：交给 spring 容器进行管理
 */
@Configuration
public class DruidConfig {

    @Bean(value = "druidDataSource",initMethod = "init",destroyMethod = "close")
    @ConfigurationProperties(prefix = "spring.druid")  // 加载配置文件
    public DruidDataSource druidDataSource(){
        DruidDataSource druidDataSource = new DruidDataSource();
        // Lists.newArrayList 相当于 new ArrayList() ，google工具包提供的
        druidDataSource.setProxyFilters(Lists.newArrayList(statFilter()));
        return druidDataSource;
    }

    // 配置过滤的数据
    @Bean
    public StatFilter statFilter(){
        StatFilter statFilter = new StatFilter();
        statFilter.setLogSlowSql(true);
        statFilter.setSlowSqlMillis(5);
        statFilter.setLogSlowSql(true);
        statFilter.setMergeSql(true);
        return statFilter;
    }

    // 配置访问路径
    @Bean
    public ServletRegistrationBean servletRegistrationBean(){
        // localhost：8888/sell/druid
        return new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
    }

}
