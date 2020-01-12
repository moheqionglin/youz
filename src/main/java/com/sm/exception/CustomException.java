package com.sm.exception;


import com.sm.message.ResultJson;

/**
 * @author Joetao
 * Created at 2018/8/24.
 */
public class CustomException extends RuntimeException {
    private ResultJson resultJson;

    public CustomException(ResultJson resultJson) {
        this.resultJson = resultJson;
    }

    public ResultJson getResultJson() {
        return resultJson;
    }

    public void setResultJson(ResultJson resultJson) {
        this.resultJson = resultJson;
    }
}
