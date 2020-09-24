package com.dounion.server.core.base;

import com.alibaba.fastjson.JSONObject;
import com.dounion.server.core.helper.ConfigurationHelper;
import com.dounion.server.core.helper.FileHelper;
import com.github.pagehelper.PageInterceptor;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableAspectJAutoProxy
@ComponentScan(value={
        "com.dounion.server.dao",
        "com.dounion.server.service",
        "com.dounion.server.controller",
        "com.dounion.server.task",
        "com.dounion.server.deploy"
})
public class BeanConfig {

    private final static Logger logger = LoggerFactory.getLogger(BeanConfig.class);

    // 服务器打包
    private final static String PATH_CONF =
            new File(System.getProperty("user.dir")).getParent() + File.separator + "conf" + File.separator;
    // 本地运行
//    private final static String PATH_CONF =
//            System.getProperty("user.dir") + File.separator + "conf" + File.separator;

    @Bean
    public ServiceInfo serverInfo() throws Exception{
        Resource resource = new FileSystemResource(PATH_CONF + "serviceInfo.json");
        String fileContent = FileHelper.readFile(resource.getInputStream());
        ServiceInfo serviceInfo = JSONObject.parseObject(fileContent, ServiceInfo.class);
        logger.info("ServiceInfo load success : \r\n {}", serviceInfo);
        // 设置运行时端口
        serviceInfo.setRunningPort(serviceInfo.getPort());
        return serviceInfo;
    }

    @Bean
    public ConfigurationHelper configBean(){
        ConfigurationHelper config = new ConfigurationHelper();
        config.setLocation(new FileSystemResource(PATH_CONF + "config_upgrade.properties"));
        return config;
    }

    @Bean
    public BasicDataSource dataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(ConfigurationHelper.getProperty("jdbc.driver_name"));
        dataSource.setUrl(ConfigurationHelper.getProperty("jdbc.driver_url"));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }


    @Bean
    public SqlSessionFactoryBean sqlSessionFactoryBean() throws IOException {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource());
        // mybatis config
        String location = ConfigurationHelper.getProperty("mapper_location");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(location);
        factoryBean.setMapperLocations(resources);
        // page config
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","false"); // 参数合理化 没必要不需开启
        properties.setProperty("helperDialect","SQLite");    //配置数据库的方言
        PageInterceptor pageHelper = new PageInterceptor();
        pageHelper.setProperties(properties);
        factoryBean.setPlugins(new Interceptor[]{pageHelper});
        return factoryBean;
    }


    @Bean
    public SqlSessionTemplate sqlSessionTemplate(){
        SqlSessionTemplate sql = null;
        try {
            sql = new SqlSessionTemplate(sqlSessionFactoryBean().getObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }

}
