package tech.finovy.distributed.id;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "tech.finovy.distributed.id.mapper")
@SpringBootApplication(scanBasePackages = {"tech.finovy.distributed.id"})
public class DistributedApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedApplication.class, args);
    }

}
