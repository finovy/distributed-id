package tech.finovy.distributed.id.constants;

public class SnowflakeConfigKeys {

    /**
     * The constant SNOWFLAKE_PREFIX.
     */
    public static final String SNOWFLAKE_PREFIX = "distributed.snowflake.";

    /**
     * 本机端口
     */
    public static final String SNOWFLAKE_PORT = SNOWFLAKE_PREFIX + "port";

    /**
     * zookeeper地址
     */
    public static final String SNOWFLAKE_ZOOKEEPER_ADDRESS = SNOWFLAKE_PREFIX + "zookeeper.address";

    /**
     * zookeeper会话超时时间
     */
    public static final String SNOWFLAKE_ZOOKEEPER_TIMEOUT_SESSION = SNOWFLAKE_PREFIX + "zookeeper.timeout.session";

    /**
     * zookeeper连接超时时间
     */
    public static final String SNOWFLAKE_ZOOKEEPER_TIMEOUT_CONNECTION = SNOWFLAKE_PREFIX + "zookeeper.timeout.connection";


}
