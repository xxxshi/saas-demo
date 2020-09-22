package com.shicc.customercenter.tenant;

import liquibase.integration.spring.SpringLiquibase;
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


    @Value("${liquibase.enabled}")
    private boolean liquibaseEnable = false;

    @Autowired
    @Lazy
    private TenantDataSourceService tenantDataSourceService;

    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private LiquibaseProperties liquibaseProperties;


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

    @Bean("tenantLiquibase")
    public SpringLiquibase tenantLiquibase() {
        SpringLiquibase liquibase = new TenantSpringLiquibase();
        liquibase.setChangeLog("classpath:liquibase/master.xml");
        liquibase.setShouldRun(liquibaseEnable);
        if (null == liquibaseProperties) {
            return liquibase;
        }
        //todo liquibaseProperties一直是null，暂时不影响使用, 待排查原因
        liquibase.setContexts(liquibaseProperties.getContexts());
        liquibase.setDefaultSchema(liquibaseProperties.getDefaultSchema());
        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
        liquibase.setChangeLogParameters(liquibaseProperties.getParameters());
        Boolean shouldRun = liquibaseEnable || liquibaseProperties.isEnabled();
        liquibase.setShouldRun(shouldRun);
        return liquibase;
    }

}