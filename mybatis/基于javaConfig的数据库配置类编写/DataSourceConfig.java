package com.springjiemi.config;

import com.github.pagehelper.PageHelper;
import com.springjiemi.util.PropertiesUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * 数据库配置
 * Created by xhsf on 2019/2/11.
 */

@Configuration
@MapperScan("com.springjiemi.dao")
public class DataSourceConfig {

    private static Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        PropertiesUtil.setProps("datasource.properties");
        dataSource.setDriverClassName(PropertiesUtil.getProperty("db.driverClassName"));
        dataSource.setUrl(PropertiesUtil.getProperty("db.url"));
        dataSource.setUsername(PropertiesUtil.getProperty("db.username"));
        dataSource.setPassword(PropertiesUtil.getProperty("db.password"));
        dataSource.setInitialSize(Integer.parseInt(PropertiesUtil.getProperty("db.initialSize")));
        dataSource.setMaxActive(Integer.parseInt(PropertiesUtil.getProperty("db.maxActive")));
        dataSource.setMaxIdle(Integer.parseInt(PropertiesUtil.getProperty("db.maxIdle")));
        dataSource.setMinIdle(Integer.parseInt(PropertiesUtil.getProperty("db.minIdle")));
        dataSource.setMaxWait(Integer.parseInt(PropertiesUtil.getProperty("db.maxWait")));
        dataSource.setDefaultAutoCommit(Boolean.parseBoolean(PropertiesUtil.getProperty("db.defaultAutoCommit")));
        dataSource.setMinEvictableIdleTimeMillis(Integer.parseInt(PropertiesUtil.getProperty("db.minEvictableIdleTimeMillis")));
        dataSource.setTimeBetweenEvictionRunsMillis(Integer.parseInt(PropertiesUtil.getProperty("db.timeBetweenEvictionRunsMillis")));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(PropertiesUtil.getProperty("db.testWhileIdle")));
        dataSource.setValidationQuery(PropertiesUtil.getProperty("db.validationQuery"));
        return  dataSource;
    }

    @Bean
    public SqlSessionFactoryBean sqlSessionFactory() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        try {
            sqlSessionFactoryBean.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources("classpath*:mappers/*Mapper.xml"));
        } catch (IOException e) {
            logger.error("配置文件加载失败", e);
        }
        //设置PageHelper组件
        Interceptor[] interceptors = new Interceptor[1];
        interceptors[0] = pageHelper();
        sqlSessionFactoryBean.setPlugins(interceptors);

        return sqlSessionFactoryBean;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource());
        dataSourceTransactionManager.setRollbackOnCommitFailure(true);
        return dataSourceTransactionManager;
    }

    @Bean
    public PageHelper pageHelper() {
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.put("dialect", "mysql");
        pageHelper.setProperties(properties);
        return new PageHelper();
    }

}
