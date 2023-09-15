package tech.finovy.distributed.id.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Ryan Luo
 * @Date: 2023/6/12 16:52
 */
@Getter
@RefreshScope
@Configuration
public class DatasourceConfiguration {

    @Value("${distributed.datasource.url:jdbc:h2:~/distributed}")
    private String url;
    @Value("${distributed.datasource.username:}")
    private String datasourceUsername;
    @Value("${distributed.datasource.password:}")
    private String datasourcePassword;


}
