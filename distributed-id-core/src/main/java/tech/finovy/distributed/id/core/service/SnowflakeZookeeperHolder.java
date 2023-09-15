package tech.finovy.distributed.id.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import tech.finovy.distributed.id.constants.Constants;
import tech.finovy.distributed.id.constants.SnowflakeConfigKeys;
import tech.finovy.distributed.id.exception.ZKException;
import tech.finovy.distributed.id.thread.NamedThreadFactory;
import tech.finovy.distributed.id.util.IPUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty("distributed.snowflake.zookeeper.address")
public class SnowflakeZookeeperHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeZookeeperHolder.class);

    /**
     * 工作id
     */
    private int workerId;

    /**
     * local ip
     */
    private final String ip;

    /**
     * local port
     */
    private final String port;

    /**
     * ip:port
     */
    private final String listenAddress;

    private final String connectionString;

    private final int connectionTimeoutMs;

    private final int sessionTimeoutMs;

    private long lastUpdateTime;

    public SnowflakeZookeeperHolder(Environment environment) {
        String ip = IPUtils.getIp();
        String port = environment.getProperty(SnowflakeConfigKeys.SNOWFLAKE_PORT, Constants.SNOWFLAKE_DEFAULT_PORT);
        String zkAddress = environment.getProperty(SnowflakeConfigKeys.SNOWFLAKE_ZOOKEEPER_ADDRESS);
        int sessionTimeoutMs =
                environment.getProperty(SnowflakeConfigKeys.SNOWFLAKE_ZOOKEEPER_TIMEOUT_SESSION, Integer.class, Constants.SNOWFLAKE_DEFAULT_ZK_TIMEOUT_SESSION);
        int connectionTimeoutMs =
                environment.getProperty(SnowflakeConfigKeys.SNOWFLAKE_ZOOKEEPER_TIMEOUT_CONNECTION, Integer.class, Constants.SNOWFLAKE_DEFAULT_ZK_TIMEOUT_CONNECTION);
        this.ip = ip;
        this.port = port;
        this.listenAddress = ip + ":" + port;
        this.connectionString = zkAddress;
        this.sessionTimeoutMs = connectionTimeoutMs;
        this.connectionTimeoutMs = sessionTimeoutMs;
    }

    public boolean init() {
        try {
            CuratorFramework curator = createWithOptions();
            curator.start();
            Stat stat = curator.checkExists().forPath(Constants.SNOWFLAKE_ZK_PATH_FOREVER);
            // zk节点
            String ZKAddressNode = null;
            // 不存在根节点,机器第一次启动
            if (stat == null) {
                // 创建/snowflake/distributed/forever/ip:port-000000000,并上传数据
                ZKAddressNode = createNode(curator);
                LOGGER.info("no root node exists, create own node on forever node and start success that endpoint ip: {} port: {} workerId: {}", ip, port, this.workerId);
            } else {
                // ip:port -> 00001
                Map<String, Integer> nodeMap = Maps.newHashMap();
                // ip:port -> (ip:port-00001)
                Map<String, String> realNode = Maps.newHashMap();
                // 存在根节点,先检查是否有属于自己的根节点
                List<String> keys = curator.getChildren().forPath(Constants.SNOWFLAKE_ZK_PATH_FOREVER);
                for (String key : keys) {
                    String[] nodeKey = key.split("-");
                    realNode.put(nodeKey[0], key);
                    nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
                }
                Integer workerId = nodeMap.get(listenAddress);
                // 存在本机节点
                if (workerId != null) {
                    // ZKAddressNode=/snowflake/distributed/forever/ip:port-000000001
                    ZKAddressNode = Constants.SNOWFLAKE_ZK_PATH_FOREVER + "/" + realNode.get(listenAddress);
                    // 启动worker时使用会使用
                    this.workerId = workerId;
                    // 检查zk上报时间戳,该时间戳不能大于当前时间
                    if (!checkInitTimeStamp(curator, ZKAddressNode)) {
                        throw new ZKException("init timestamp check error, forever node timestamp greater than current time");
                    }
                    LOGGER.info("find forever node have this endpoint ip: {} port: {} workerId: {} child node and start success", ip, port, this.workerId);
                } else {
                    // 表示新启动的节点,创建节点,不用check时间
                    ZKAddressNode = createNode(curator);
                    String[] nodeKey = ZKAddressNode.split("-");
                    this.workerId = Integer.parseInt(nodeKey[1]);
                    LOGGER.info("can not find node on forever node that endpoint ip: {} port: {} workerId: {}, create own node on forever node and start success ", ip, port, this.workerId);
                }
            }
            // 在本机存储一个临时文件 workerId默认是0
            updateLocalWorkerID(workerId);
            // 定时上报本机时间给临时节点
            ScheduledUploadData(curator, ZKAddressNode);
        } catch (Exception e) {
            LOGGER.error("start node error", e);
            try {
                Properties properties = new Properties();
                properties.load(Files.newInputStream(new File(Constants.SNOWFLAKE_TMP_PATH.replace("{port}", port + "")).toPath()));
                workerId = Integer.parseInt(properties.getProperty("workerId"));
                LOGGER.warn("start failed, use local node file properties workerId-{}", workerId);
            } catch (Exception e1) {
                LOGGER.error("read file error ", e1);
                return false;
            }
        }
        return true;
    }

    private void ScheduledUploadData(final CuratorFramework curator, final String ZKAddressNode) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Thread-Schedule-Upload", 1));
        // 延迟1s后,每3s上报一次数据
        executorService.scheduleWithFixedDelay(() -> updateNodeData(curator, ZKAddressNode), 1L, 3L, TimeUnit.SECONDS);
    }

    private boolean checkInitTimeStamp(CuratorFramework curator, String ZKAddressNode) throws Exception {
        byte[] bytes = curator.getData().forPath(ZKAddressNode);
        Endpoint endPoint = deBuildData(new String(bytes));
        // 该节点的时间不能小于最后一次上报的时间
        return !(endPoint.getTimestamp() > System.currentTimeMillis());
    }

    /**
     * 创建持久化顺序节点,并把节点数据放入 value
     */
    private String createNode(CuratorFramework curator) throws Exception {
        try {
            return curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL)
                    .forPath(Constants.SNOWFLAKE_ZK_PATH_FOREVER + "/" + listenAddress + "-", buildData().getBytes());
        } catch (Exception e) {
            LOGGER.error("create node error msg {} ", e.getMessage());
            throw e;
        }
    }

    private void updateNodeData(CuratorFramework curator, String path) {
        try {
            if (System.currentTimeMillis() < lastUpdateTime) {
                return;
            }
            curator.setData().forPath(path, buildData().getBytes());
            lastUpdateTime = System.currentTimeMillis();
        } catch (Exception e) {
            LOGGER.info("update data error path is {} error is {}", path, e);
        }
    }

    /**
     * 构建需要上传的数据
     *
     * @return
     */
    private String buildData() throws JsonProcessingException {
        Endpoint endpoint = new Endpoint(ip, port, System.currentTimeMillis());
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(endpoint);
    }

    private Endpoint deBuildData(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Endpoint.class);
    }

    /**
     * 在节点文件系统上缓存一个workID值,zk失效,机器重启时保证能够正常启动
     */
    private void updateLocalWorkerID(int workerID) {
        File config = new File(Constants.SNOWFLAKE_TMP_PATH.replace("{port}", port));
        boolean exists = config.exists();
        LOGGER.info("file exists status is {}", exists);
        if (exists) {
            try {
                FileUtils.writeStringToFile(config, "workerId=" + workerID, false);
                LOGGER.info("update file cache workerId is {}", workerID);
            } catch (IOException e) {
                LOGGER.error("update file cache error ", e);
            }
        } else {
            //不存在文件,父目录页肯定不存在
            try {
                boolean mkdirs = config.getParentFile().mkdirs();
                LOGGER.info("init local file cache create parent dis status is {}, worker id is {}", mkdirs, workerID);
                if (mkdirs) {
                    if (config.createNewFile()) {
                        FileUtils.writeStringToFile(config, "workerId=" + workerID, false);
                        LOGGER.info("local file cache workerId is {}", workerID);
                    }
                } else {
                    LOGGER.warn("create parent dir error===");
                }
            } catch (IOException e) {
                LOGGER.warn("create workerId conf file error", e);
            }
        }
    }

    private CuratorFramework createWithOptions() {
        return CuratorFrameworkFactory.builder().connectString(this.connectionString)
                .retryPolicy(new RetryUntilElapsed(1000, 4))
                .connectionTimeoutMs(this.connectionTimeoutMs)
                .sessionTimeoutMs(this.sessionTimeoutMs)
                .build();
    }

    public int getWorkerId() {
        return workerId;
    }

    /**
     * 上报数据结构
     */
    static class Endpoint {
        private String ip;
        private String port;
        private long timestamp;

        public Endpoint() {
        }

        public Endpoint(String ip, String port, long timestamp) {
            this.ip = ip;
            this.port = port;
            this.timestamp = timestamp;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }

}
