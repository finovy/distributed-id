package tech.finovy.distributed.id.constants;

import java.io.File;
import java.nio.charset.Charset;

public class Constants {

    /**
     * 最大步长不超过100,0000
     */
    public static final int SEGMENT_MAX_STEP = 1000000;

    /**
     * 一个Segment维持时间为15分钟
     */
    public static final long SEGMENT_DURATION = 15 * 60 * 1000L;

    /**
     * The constant DEFAULT_SEGMENT_DB_TABLE_NAME.
     */
    public static final String SEGMENT_DEFAULT_TABLE_NAME = "distributed_id_alloc";

    public static final String SEGMENT_DEFAULT_DB_TYPE = "mysql";


    /**
     * redis特定前缀
     */
    public static final String REDIS_DEFAULT_PREFIX = "distributed:id:";


    /**
     * 雪花算法相关常量
     */
    public static final long SNOWFLAKE_WORKER_ID_BITS = 10L;

    /**
     * 雪花算法最大能够分配的workerId=1024
     */
    public static final long SNOWFLAKE_MAX_WORKER_ID = ~(-1L << SNOWFLAKE_WORKER_ID_BITS);

    /**
     * 雪花算法序号位数=12
     */
    public static final long SNOWFLAKE_SEQUENCE_BITS = 12L;

    /**
     * 雪花算法机器码左移12
     */
    public static final long SNOWFLAKE_WORKER_ID_SHIFT = SNOWFLAKE_SEQUENCE_BITS;

    /**
     * 雪花算法时间戳左移22
     */
    public static final long SNOWFLAKE_TIMESTAMP_LEFT_SHIFT = SNOWFLAKE_SEQUENCE_BITS + SNOWFLAKE_WORKER_ID_BITS;

    /**
     * 雪花算法序号位于运算
     */
    public static final long SNOWFLAKE_SEQUENCE_MASK = ~(-1L << SNOWFLAKE_SEQUENCE_BITS);

    public static final String SNOWFLAKE_DEFAULT_PORT = "8080";

    /**
     * default zookeeper会话超时时间
     */
    public static final int SNOWFLAKE_DEFAULT_ZK_TIMEOUT_SESSION = 6 * 1000;

    /**
     * default zookeeper连接超时时间
     */
    public static final int SNOWFLAKE_DEFAULT_ZK_TIMEOUT_CONNECTION = 15 * 1000;

    /**
     * zookeeper地址前缀
     */
    public static final String SNOWFLAKE_ZK_PATH_PREFIX = "/snowflake/distributed";

    /**
     * 本机存储数据的地址
     */
    public static final String SNOWFLAKE_TMP_PATH = System.getProperty("java.io.tmpdir") + File.separator + "distributed/conf/{port}/workerID.properties";

    /**
     * 保存所有数据持久化的节点
     */
    public static final String SNOWFLAKE_ZK_PATH_FOREVER = SNOWFLAKE_ZK_PATH_PREFIX + "/forever";

    /**
     * default charset name
     */
    public static final String DEFAULT_CHARSET_NAME = "UTF-8";

    /**
     * default charset is utf-8
     */
    public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

    public static final String DATA_REFRESH_TYPE = "DATE_REFRESH_TYPE";

}
