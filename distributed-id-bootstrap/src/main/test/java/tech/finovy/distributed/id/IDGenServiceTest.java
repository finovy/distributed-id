package tech.finovy.distributed.id;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;
import tech.finovy.distributed.id.core.service.RedisServiceImpl;
import tech.finovy.distributed.id.core.service.SegmentServiceImpl;
import tech.finovy.distributed.id.core.service.SnowflakeServiceImpl;
import tech.finovy.distributed.id.mapper.IDPersistMapper;

import java.util.ArrayList;
import java.util.List;

import static tech.finovy.distributed.id.util.WrapperUtil.wrapper;
import static tech.finovy.distributed.id.util.WrapperUtil.wrapperList;


@Slf4j
@MapperScan(basePackages = "tech.finovy.distributed.id.mapper")
@ComponentScan(basePackages = {"tech.finovy.distributed.id"})
@ContextConfiguration
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@ImportAutoConfiguration({RefreshAutoConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = IDGenServiceTest.class)
public class IDGenServiceTest {

    public static final String key = "id-h2-local";
    public static final Integer round = 1;

    @Autowired
    private SegmentServiceImpl segmentService;

    @Test
    public void segmentServiceTest() {
        for (int i = 0; i < round; i++) {
            final List<Long> ids = segmentService.getIds(key, 2);
            log.info("Round:{} fetch-id:{}", i + 1, ids);
        }
    }

    @Autowired
    private SnowflakeServiceImpl snowflakeService;


    @Test
    public void snowflakeServiceTest() {
        for (int i = 0; i < round; i++) {
            final List<Long> ids = snowflakeService.getIds(key, 2);
            log.info("Round:{} fetch-id:{}", i + 1, ids);
        }
    }

    @Autowired
    private RedisServiceImpl redisService;

    @Test
    public void redisServiceTest() {
        for (int i = 0; i < round; i++) {
            final List<Long> ids = redisService.getIds(key, 1000);
            log.info("Round:{} fetch-id:{}", i + 1, ids);
        }
    }

    @Autowired
    private IDPersistMapper mapper;


    @Test
    @SneakyThrows
    public void joinPrefixTest() {
        // step A
        Long id = 123456L;
        final String id_A = wrapper(id, "SN", 10);
        log.info("> target {}", id_A);
        Assert.isTrue(id_A.length() == 10, "unit test fail");
        final String id_B = wrapper(id, "SN", 5);
        log.info("< target {}", id_B);
        Assert.isTrue(id_B.length() == 8, "unit test fail");
        final String id_C = wrapper(id, "SN", -1);
        log.info("< target {}", id_C);
        Assert.isTrue(id_C.length() == 8, "unit test fail");
        // step B
        List<Long> list = new ArrayList<>();
        list.add(123456L);
        list.add(421456L);
        final List<String> id_A_L = wrapperList(list, "SN", 10);
        for (String v : id_A_L) {
            log.info("> target {}", v);
            Assert.isTrue(v.length() == 10, "unit test fail");
        }
        final List<String> id_B_L = wrapperList(list, "SN", 5);
        for (String v : id_B_L) {
            log.info("> target {}", v);
            Assert.isTrue(v.length() == 8, "unit test fail");
        }
        final List<String> id_C_L = wrapperList(list, "SN", -1);
        for (String v : id_C_L) {
            log.info("> target {}", v);
            Assert.isTrue(v.length() == 8, "unit test fail");
        }
    }
}

