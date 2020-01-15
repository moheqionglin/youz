package com.sm.dao;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2019-05-23 11:06
 */
@Configuration
@ComponentScan(basePackages = {"com.sm.dao"}, excludeFilters={@ComponentScan.Filter(type= FilterType.ANNOTATION,value= Configuration.class)})
@EnableTransactionManagement
public class DaoConfig {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String user ;
    @Value("${spring.datasource.password}")
    private String pwd ;
    @Value("${sm.connection.initialSize:1}")
    private int defaultInitialSize = 1;
    @Value("${sm.connection.minIdle:10}")
    private int defaultMinIdle = 1;
    @Value("${sm.connection.maxActive:10}")
    private int defaultMaxActive = 10;
    @Value("${sm.connection.maxWait:60000}")
    private int defaultMaxWait = 60000;
    @Value("${sm.connection.maxPoolPreparedStatementPerConnectionSize:50}")
    private int maxPoolPreparedStatementPerConnectionSize = 20;

    /**
     *  spring.io Dao says , the best practice is every dao have it's own jdbctemplate
     */
    @Bean
    @Scope("prototype")
    public JdbcTemplate jdbcTemplate(DruidDataSource datasourceRouter){
        return new JdbcTemplate(datasourceRouter);
    }
    /**
     *  spring.io Dao says, the best practice is every dao have it's own jdbctemplate
     */
    @Bean
    @Scope("prototype")
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DruidDataSource datasourceRouter){
        return new NamedParameterJdbcTemplate(datasourceRouter);
    }

    @Bean
    public YzDruidErrorFilter slf4jFilter(){
        YzDruidErrorFilter ef = new YzDruidErrorFilter();
        ef.setStatementExecutableSqlLogEnable(true);
        return ef;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DruidDataSource datasourceRouter){
        return new DataSourceTransactionManager(datasourceRouter);
    }

    @Bean(name = "defaultDatasource", initMethod = "init", destroyMethod = "close")
    public DruidDataSource datasource(YzDruidErrorFilter slf4jFilter){
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");

        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pwd);
        ds.setInitialSize(defaultInitialSize);
        ds.setMinIdle(defaultMinIdle);
        ds.setMaxActive(defaultMaxActive);
        ds.setMaxWait(defaultMaxWait);
        ds.setTimeBetweenEvictionRunsMillis(60000);
        ds.setMinEvictableIdleTimeMillis(300000);
        ds.setValidationQuery("select 1 from information_schema.tables limit 1");
        ds.setTestWhileIdle(true);
        ds.setTestOnBorrow(false);
        ds.setTestOnReturn(false);
        ds.setPoolPreparedStatements(false);
        ds.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        List<Filter> filters = new ArrayList<>();
        filters.add(slf4jFilter);
        ds.setProxyFilters(filters);
        return ds;
    }
}