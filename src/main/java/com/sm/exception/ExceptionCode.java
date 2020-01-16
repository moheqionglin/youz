package com.sm.exception;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 22:24
 */
public enum ExceptionCode {

    WECHART_LOGIN_ERROR(1000, "wechart get session error : "),
    WECHART_LOGIN_RESULT_ERROR(1001, "wechart result error : ");
    private int code;
    private String errorStr;

    private ExceptionCode(int code, String errorStr) {
        this.code = code;
        this.errorStr = errorStr;
    }

    public String getErrorStr() {
        return this.errorStr;
    }

    public int getErrorCode() {
        return this.code;
    }
}