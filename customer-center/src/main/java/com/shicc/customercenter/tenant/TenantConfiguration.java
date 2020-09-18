package com.shicc.customercenter.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Configuration
@EnableAsync
@ConditionalOnProperty(prefix = "spring.jpa.properties.hibernate", name = {"multiTenancy", "tenant_identifier_resolver", "multi_tenant_connection_provider"})
@Order(-1)
public class TenantConfiguration  implements ApplicationRunner, ServletContextInitializer {

    private static final Logger log = LoggerFactory.getLogger(TenantConfiguration.class);


    @Value("${liquibase.enable:false}")
    private boolean liquibaseEnable;

    @Autowired
    @Lazy
    private TenantDataSourceService tenantDataSourceService;

    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    @Autowired
    private DataSource dataSource;


    @Value("${tenancy.interceptor.excludes:}")
    private String userExcludes;


    /**
     * 服务启动后初始化远程配置
     *
     * @param applicationArguments
     */
    @Override
    public void run(ApplicationArguments applicationArguments) {
        log.debug("init datasource on Application started");
        tenantDataSourceService.initRemoteDataSource();
        log.debug("init remote datasource success");
    }


    @Override
    public void onStartup(ServletContext servletContext)  {
        if (null == TenantDataSourceProvider.getTenantDataSource(TenantDataSourceProvider.DEFAULT_KEY)) {
            log.info("init defaultDatasource on Application starting");
            tenantDataSourceProvider.addDefaultDataSource(dataSource);
        } else {
            log.debug("defaultDatasource has init success.");
        }
    }

}