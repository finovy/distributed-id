package tech.finovy.distributed.id.config;

import com.alibaba.druid.pool.DruidDataSource;
import tech.finovy.framework.common.SecurityEncryption;
import tech.finovy.framework.datasource.pools.DynamicDataSouceMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.ObjectUtils;
import tech.finvoy.framework.datasource.common.entity.DynamicDatasourceConfig;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/12 16:51
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "distributed.datasource.url")
public class DatasourceManager {

    private final DatasourceConfiguration configuration;

    private static final String DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY = "DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY";

    public DatasourceManager(DatasourceConfiguration configuration) {
        this.configuration = configuration;
    }

    @Autowired
    private DruidDataSource dataSource;

    @Bean
    public DruidDataSource distributedIdConfigurationDataSource() throws SQLException {
        DynamicDataSouceMap dynamicDataSourceMap = new DynamicDataSouceMap();
        if (dynamicDataSourceMap.getDatasource(DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY) != null) {
            return dynamicDataSourceMap.getDatasource(DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY);
        }
        if (ObjectUtils.isEmpty(dynamicDataSourceMap.getDatasource(DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY))) {
            DynamicDatasourceConfig config = new DynamicDatasourceConfig();
            config.setKey(DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY);
            config.setUrl(configuration.getUrl());
            config.setUsername(configuration.getDatasourceUsername());
            config.setPassword(configuration.getDatasourcePassword());
            try {
                encrypt(config);
            } finally {
                dynamicDataSourceMap.refreshDatasource(config);
            }
        }
        dataSource = dynamicDataSourceMap.getDatasource(DISTRIBUTED_ID_SEGMENT_DATASOURCE_KEY);
        return dataSource;
    }

    @SneakyThrows
    @Bean
    public SqlSessionFactory sqlSessionFactory() {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setTransactionFactory(new SpringManagedTransactionFactory());
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:tech.finovy/framework/distributed/id/mapper/**/*.java"));
        return sessionFactory.getObject();
    }


    private void encrypt(DynamicDatasourceConfig config) {
        String dconfSecret = System.getenv("DCONF_SECRET");
        String dconfIv = System.getenv("DCONF_IV");
        if (StringUtils.isBlank(dconfSecret)) {
            return;
        }
        try {
            config.setUrl(SecurityEncryption.decrypt(config.getUrl(), dconfSecret, dconfIv));
            config.setUsername(SecurityEncryption.decrypt(config.getUsername(), dconfSecret, dconfIv));
            config.setPassword(SecurityEncryption.decrypt(config.getPassword(), dconfSecret, dconfIv));
        } catch (BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | IllegalBlockSizeException |
                 NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            log.error(e.toString());
        }
    }

}
