package com.sm.exception;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 19:12
 */
public class SmException extends Exception {
    private int code;

    public SmException(int code, String message) {
        this(code, message, (Throwable)null);
    }

    public SmException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }
    public int getCode() {
        return this.code;
    }
}
