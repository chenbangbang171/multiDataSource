package com.example.multidatasource.dataSourceConfig;

import com.alibaba.druid.pool.DruidDataSource;
import com.example.multidatasource.aop.DataSourceAsp;
import com.example.multidatasource.util.DataSourceSwitcher;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.HashMap;

@Configuration
@MapperScan(basePackages="com.example.multidatasource.mapper", sqlSessionTemplateRef = "mysqlSqlSessionTemplate") //扫描到具体的包
public class DataSourceSwitchConfig {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    //这里写你的mapper接口对应的mapper.xml的路径,我这里是配置在项目的resources资源文件下
    private static final String MAPPER_LOCATION = "classpath:mapper/*.xml";
    @Autowired
    private MysqlDataSourceConfig mysqlDataSourceConfig;
    @Autowired
    private MysqlDataSourceConfig2 mysqlDataSourceConfig2;

    @Bean(name = "dataSourceSwitcher")
    @Primary
    public DataSourceSwitcher dataSourceSwitcher() {
        DataSourceSwitcher dataSourceSwitcher = new DataSourceSwitcher();
        HashMap<Object, Object> targetDataSources = new HashMap<>();
        DruidDataSource dataSource1 = mysqlDataSourceConfig.dataSource();
        DruidDataSource dataSource2 = mysqlDataSourceConfig2.dataSource2();
        targetDataSources.put("ds1",dataSource1);
        targetDataSources.put("ds2",dataSource2);
        dataSourceSwitcher.setTargetDataSources(targetDataSources);
        dataSourceSwitcher.setDefaultTargetDataSource(dataSource1);
        return dataSourceSwitcher;
    }


    @Bean(name = "multiDataSourceAsp")
    @Primary
    public DataSourceAsp multiDataSourceAsp(){
        DataSourceAsp dataSourceAsp = new DataSourceAsp();
        dataSourceAsp.setmDataSourceSwitcher(dataSourceSwitcher());
        return dataSourceAsp;
    }


    /**
     * 设置数据源的事务
     */
    @Bean(name = "transactionManager")
    @Primary
    public DataSourceTransactionManager transactionManager() {
        log.info("---------mysqlTransactionManager-------" + "数据源的事务加载完成");
        return new DataSourceTransactionManager(dataSourceSwitcher());
    }


    /**
     * 连接池管道
     */
    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory()
            throws Exception {
        log.info("--------mysqlSqlSessionFactory-------" + "连接池管道加载完成");
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSourceSwitcher());
        sessionFactory.setMapperLocations(
                new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        return sessionFactory.getObject();
    }


    /**
     * 数据sql模板
     */
    @Bean(name = "mysqlSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate kdysSqlSessionTemplate( SqlSessionFactory sqlSessionFactory)  {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /***
     *  配置多数据源 博客地址https://blog.csdn.net/laojiaqi/article/details/78964862
     *
     *
     * */

}
