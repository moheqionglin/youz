package com.sm.util;

import com.sm.exception.ExceptionCode;
import com.sm.exception.SmException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 19:10
 */
public class ExceptionUtil {
    public static void checkCondition(boolean valid, ExceptionCode code, String extraMsg) throws SmException {
        if (!valid) {
            throw new SmException(code.getErrorCode(), code.getErrorStr() + extraMsg);
        }
    }

    public static void checkCondition(boolean valid, ExceptionCode code, String extraMsg, Exception e) throws SmException {
        if (!valid) {
            throw new SmException(code.getErrorCode(), code.getErrorStr() + extraMsg, e);
        }
    }
}