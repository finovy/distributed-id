package tech.finovy.distributed.id.constants;

public enum Status {

    SUCCESS(0, "success"),

    /**
     * 未初始化成功时的异常码
     */
    EXCEPTION_INIT_FALSE(-1, "init false"),

    /**
     * key不存在时的异常码
     */
    EXCEPTION_KEY_NOT_EXISTS(-2, "key not exists"),

    /**
     * SegmentBuffer中的两个Segment均未从DB中装载时的异常码
     */
    EXCEPTION_SEGMENTS_IS_NULL(-3, "segments is null"),

    /**
     * SNOWFLAKE本机时间戳异常
     */
    EXCEPTION_SNOWFLAKE_TIMESTAMP(-4, "snowflake timestamp exception"),

    EXCEPTION_REDIS_DISABLED(-5, "redis service disabled"),

    EXCEPTION_SEGMENT_DISABLED(-6, "segment service disabled"),

    EXCEPTION_SNOWFLAKE_DISABLED(-7, "snowflake service disabled");

    final int code;

    final String message;

    Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
