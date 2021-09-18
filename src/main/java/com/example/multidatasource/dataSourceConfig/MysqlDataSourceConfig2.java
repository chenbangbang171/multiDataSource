package com.example.multidatasource.dataSourceConfig;
import com.alibaba.druid.pool.DruidDataSource;
import com.example.multidatasource.util.DataSourceSwitcher;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;


/**
 * @Description mysql数据源
 * @Author  ouyangli
 * @Param
 * @Return
 * @Date 2019/4/16 0016 13:41
 */

@Configuration
//这里写你的这个数据源需要使用的mapper接口的包路径
@MapperScan(basePackages="com.example.multidatasource.mapper", sqlSessionTemplateRef = "mysqlSqlSessionTemplate") //扫描到具体的包
public class MysqlDataSourceConfig2 {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${datasource-mysql.url2}")
    private String dbUrl;
    @Value("${datasource-mysql.type2}")
    private String dbtype;
    @Value("${datasource-mysql.username2}")
    private String username;
    @Value("${datasource-mysql.password2}")
    private String password;
    @Value("${datasource-mysql.driverClassName2}")
    private String driverClassName;
    @Value("${datasource-mysql.validationQuery2}")
    private String validationQuery;
    //以下的配置从配置文件中读取
    @Value("${dbPool.initialSize}")
    private int initialSize;
    @Value("${dbPool.minIdle}")
    private int minIdle;
    @Value("${dbPool.maxActive}")
    private int maxActive;
    @Value("${dbPool.maxWait}")
    private int maxWait;
    @Value("${dbPool.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
    @Value("${dbPool.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;
    @Value("${dbPool.testWhileIdle}")
    private boolean testWhileIdle;
    @Value("${dbPool.testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${dbPool.testOnReturn}")
    private boolean testOnReturn;
    @Value("${dbPool.poolPreparedStatements}")
    private boolean poolPreparedStatements;
    @Value("${dbPool.maxPoolPreparedStatementPerConnectionSize}")
    private int maxPoolPreparedStatementPerConnectionSize;

    /**
     * 设置数据源的参数
     */
    @Bean
    public DruidDataSource dataSource2() {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(this.dbUrl);
        datasource.setDbType(dbtype);
        datasource.setUsername(this.username);
        datasource.setPassword(this.password);
        datasource.setDriverClassName(this.driverClassName);
        datasource.setInitialSize(this.initialSize);
        datasource.setMinIdle(this.minIdle);
        datasource.setMaxActive(this.maxActive);
        datasource.setMaxWait((long) this.maxWait);
        datasource.setTimeBetweenEvictionRunsMillis((long) this.timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis((long) this.minEvictableIdleTimeMillis);
        datasource.setValidationQuery(this.validationQuery);
        datasource.setTestWhileIdle(this.testWhileIdle);
        datasource.setTestOnBorrow(this.testOnBorrow);
        datasource.setTestOnReturn(this.testOnReturn);
        datasource.setPoolPreparedStatements(this.poolPreparedStatements);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(this.maxPoolPreparedStatementPerConnectionSize);
        return datasource;
    }

}


