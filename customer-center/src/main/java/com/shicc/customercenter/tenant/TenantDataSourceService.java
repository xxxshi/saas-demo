package com.shicc.customercenter.tenant;

import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shicc.customercenter.tenant.TenantConfigProperties.DEFAULT_REQUEST;
import static com.shicc.customercenter.tenant.TenantRemote.TenantMethods.QUERY_ALL_DATA_SOURCE;
import static org.springframework.http.HttpMethod.GET;

@Service
@EnableScheduling
public class TenantDataSourceService {
    private static final Logger log = LoggerFactory.getLogger(TenantDataSourceService.class);

    private static final String QUERY_SCHEMA_SQL = "SELECT count(sc.SCHEMA_NAME) FROM information_schema.SCHEMATA sc where sc.SCHEMA_NAME=?";
    private static final String CREATE_DATABASE = "create database ";
    private static final String DEFAULT_CHARACTER_SET = " DEFAULT  character set ";
    private static final String MYSQL_SPEC = "`";
    private static final String USE = "use ";
    private static final String ALTER_DATABASE = "alter database ";
    private static final String CHARACTER_SET = " character set ";

    @Value("${spring.datasource.charset:}")
    private String charset ;

    private static final String defaultCharset = " utf8mb4";

    @Autowired
    private TenantDataSourceProvider tenantDataSourceProvider;

    private static final ParameterizedTypeReference<List<TenantDataSourceInfoDTO>> LIST_DATA_SOURCE_INFO = new ParameterizedTypeReference<List<TenantDataSourceInfoDTO>>() {
    };

    private static final ParameterizedTypeReference<TenantDataSourceInfoDTO> DATA_SOURCE_INFO = new ParameterizedTypeReference<TenantDataSourceInfoDTO>() {
    };

    @Autowired
    @Qualifier("tenantLiquibase")
    private SpringLiquibase tenantLiquibase;

    @Autowired(required = false)
    private LiquibaseProperties liquibaseProperties;

    @Value("${eureka.instance.appname}")
    private String APP_NAME;
    private final String BASE_URL;
    private final RestTemplate REST_TEMPLATE;

    public TenantDataSourceService(TenantConfigProperties tenantConfigProperties) {
        this.APP_NAME = tenantConfigProperties.getAppName();
        this.BASE_URL = tenantConfigProperties.getSaasTenantBaseUrl();
        this.REST_TEMPLATE = tenantConfigProperties.getUaaRestTemplate();
    }


    /**
     * 初始化远程数据源
     */
    public void initRemoteDataSource() {
        if (StringUtils.isEmpty(APP_NAME)) {
            log.warn("server Name is null can't init remote dataSource");
            return;
        }
        try {
            List<TenantDataSourceInfoDTO> dataSourceInfoList = this.getRemoteDataSource();
            if (CollectionUtils.isEmpty(dataSourceInfoList)) {
                log.warn("remote dataSource is empty");
                return;
            }
            dataSourceInfoList.forEach(dataSourceInfo -> {
                addRemoteDataSource(dataSourceInfo);
                if ("mysql".equalsIgnoreCase(dataSourceInfo.getType())) {
                    initDatabaseBySpringLiquibase(TenantDataSourceProvider.getTenantDataSource(dataSourceInfo.getTenantCode()));
                }
            });
        } catch (Exception e) {
            log.error("init remote dataSource error. {}", e);
        }
    }

    /**
     * 添加数据源
     *
     * @param dataSourceInfo
     */
    public void addRemoteDataSource(TenantDataSourceInfoDTO dataSourceInfo) {
        log.info("addDataSource :{} ", dataSourceInfo);
        if (null == dataSourceInfo) {
            log.warn("remote datasource is empty.");
            return;
        }
        if ("mysql".equalsIgnoreCase(dataSourceInfo.getType())) {
            tenantDataSourceProvider.addDataSource(dataSourceInfo);
        }
    }


    /**
     * 根据服务名称从远程获取数据源
     *
     * @return
     */
    public  List<TenantDataSourceInfoDTO> getRemoteDataSource() {
        List<TenantDataSourceInfoDTO> result = REST_TEMPLATE.exchange(getRequestUri(QUERY_ALL_DATA_SOURCE, APP_NAME), GET, DEFAULT_REQUEST, LIST_DATA_SOURCE_INFO).getBody();

//        String url = "http://localhost:8002/api/tenant-data-source-infos/serverName?serverName={serverName}";
//        List<TenantDataSourceInfoDTO> result = restTemplate.exchange(url, GET, formEntity, LIST_DATA_SOURCE_INFO, parm).getBody();

        return result;
    }

    /**
     * 获取请求url
     *
     * @param method
     * @param replace
     * @return
     */
    private String getRequestUri(String method, Object... replace) {
        if (null == replace) {
            return BASE_URL + method;
        }
        return BASE_URL + String.format(method, replace);
    }


    /**
     * 初始化数据源,且添加初始化的数据源         ps: 新增租户或租户的对应的数据库连接信息发生改变
     *
     * @param dataSourceInfo
     */
    public void initDatabase(TenantDataSourceInfoDTO dataSourceInfo) {
        log.info("init multi database :{} ", dataSourceInfo);
        DataSource dataSource = null;
        try {
            try {
                //尝试连接租户指定数据源
                dataSource = tenantDataSourceProvider.genDataSource(dataSourceInfo);
                //初始化数据源
                initDatabaseBySpringLiquibase(dataSource);
                //添加数据源
                addRemoteDataSource(dataSourceInfo);
            } catch (Exception e) {
                log.error("init liquibase error first: {}", e);
                //添加数据源失败，则在默认连接的mysql服务器上新建数据库
                //数据库
                final Connection defaultConnection = TenantDataSourceProvider.getTenantDataSource(TenantDataSourceProvider.DEFAULT_KEY).getConnection();
                //如果数据库不存在则在默认连接的mysql服务器上新建数据库
                this.useTenantDB(dataSourceInfo.getTenantCode(), dataSourceInfo.getDatabase(), defaultConnection);
                //尝试连接租户指定数据源
                dataSource = tenantDataSourceProvider.genDataSource(dataSourceInfo);
                //初始化数据源
                initDatabaseBySpringLiquibase(dataSource);
                //添加数据源
                addRemoteDataSource(dataSourceInfo);
            }
        } catch (SQLException e) {
            log.error("init liquibase error second: {}", e);
        }
    }

    /**
     * 使用liquibase 初始化数据库
     *
     * @param dataSource
     */
    private void initDatabaseBySpringLiquibase(DataSource dataSource) {
        log.debug("current DataSource:{}", dataSource);
        if (null == tenantLiquibase ) {
            log.warn("springLiquibase is null can't init multi");
            return;
        }
        try {
            log.info("start init multi by liquibase");
            tenantLiquibase.setDataSource(dataSource);
            tenantLiquibase.afterPropertiesSet();
            log.info("success init multi by liquibase");
        } catch (Exception e) {
            log.error("springLiquibase init multi failed, {}", dataSource, e);
        }
    }

    /**
     * 使用租户数据库
     * 如果数据库不存在则在默认连接的mysql服务器上新建数据库
     *
     * @param tenantIdentifier
     * @param dbName
     * @param connection
     * @return 是否需要初始化数据库
     */
    private boolean useTenantDB(String tenantIdentifier, String dbName, Connection connection) {
        if (TenantDataSourceProvider.DEFAULT_KEY.equals(tenantIdentifier)) {
            return false;
        }
        if (!StringUtils.hasText(dbName)) {
            log.error("cannot get dbName");
            return false;
        }
        boolean result = false;
        try {
            log.info("dbName:{}", dbName);
            PreparedStatement ps = connection.prepareStatement(QUERY_SCHEMA_SQL);
            ps.setString(1, dbName);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                log.info("db:{} is not exist, system auto create:{}", dbName, dbName);

                if (StringUtils.isEmpty(charset)) {
                    charset = defaultCharset;
                    log.debug("charset:{}", charset);
                }
                String createSQL = CREATE_DATABASE + MYSQL_SPEC + dbName + MYSQL_SPEC + DEFAULT_CHARACTER_SET + charset;

                log.info("createSQL:{}", createSQL);
                connection.createStatement().execute(createSQL);
                result = true;
            }
            connection.createStatement().execute(USE + MYSQL_SPEC + dbName + MYSQL_SPEC);
        } catch (Exception e) {
            log.error("connect to DB:{} failed, connection:{}, exception:{}", dbName, connection, e);
        }
        return result;
    }



}
