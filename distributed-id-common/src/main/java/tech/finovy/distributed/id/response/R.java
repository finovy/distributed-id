package tech.finovy.distributed.id.response;

import java.io.Serializable;

public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private int code;
    private boolean success = true;
    private T data;
    private String msg;

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        if (code != 0) {
            this.success = false;
        }
    }

    public static <T> R<T> data(T data) {
        return data(data, "success");
    }

    public static <T> R<T> data(T data, String msg) {
        return data(0, data, msg);
    }

    public static <T> R<T> data(T data, int code) {
        return new R(code, data, "success");
    }

    public static <T> R<T> data(int code, T data, String msg) {
        return new R(code, data, data == null ? "data null" : msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R(code, (Object) null, msg);
    }

    public int getCode() {
        return this.code;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public T getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }


    public void setCode(final int code) {
        this.code = code;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public String toString() {
        return "R(code=" + this.getCode() + ", success=" + this.isSuccess() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
    }

}
