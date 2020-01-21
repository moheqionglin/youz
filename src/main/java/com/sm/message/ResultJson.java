package com.sm.message;


import com.sm.controller.HttpYzCode;

import java.io.Serializable;

/**
 * @author willy
 * RESTful API 返回类型
 */
public class ResultJson<T> implements Serializable {

    private static final long serialVersionUID = 783015033603078674L;
    private int code;
    private String msg;
    private T data;
    private  HttpYzCode hcode;

    public static ResultJson ok() {
        return ok("");
    }

    public static ResultJson ok(Object o) {
        return new ResultJson(HttpYzCode.SUCCESS, o);
    }

    public static ResultJson failure(HttpYzCode code) {
        return failure(code, "");
    }

    public static ResultJson failure(HttpYzCode code, Object o) {
        return new ResultJson(code, o);
    }

    private ResultJson (HttpYzCode resultCode) {
        hcode = resultCode;
        setResultCode(resultCode);
    }

    private ResultJson (HttpYzCode resultCode,T data) {
        hcode = resultCode;
        setResultCode(resultCode);
        this.data = data;
    }

    private void setResultCode(HttpYzCode resultCode) {
        hcode = resultCode;
        this.code = resultCode.getCode();
        this.msg = resultCode.getMesssage();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public HttpYzCode getHcode() {
        return hcode;
    }

    public void setHcode(HttpYzCode hcode) {
        this.hcode = hcode;
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"msg\":\"" + msg + '\"' +
                ", \"data\":\"" + data + '\"'+
                '}';
    }
}
