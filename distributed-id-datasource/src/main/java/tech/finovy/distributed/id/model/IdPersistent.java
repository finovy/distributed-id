package tech.finovy.distributed.id.model;

public class IdPersistent {

    private String key;

    private long maxId;

    private Integer redisStatus;

    private String updateTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getMaxId() {
        return maxId;
    }

    public void setMaxId(long maxId) {
        this.maxId = maxId;
    }

    public int getRedisStatus() {
        return redisStatus;
    }

    public void setRedisStatus(int redisStatus) {
        this.redisStatus = redisStatus;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
