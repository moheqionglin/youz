package com.sm.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
public class TraceIdUtils {
    private static final Logger logger = LoggerFactory.getLogger(TraceIdUtils.class);
    //MDC key
    public static final String TRACE_ID = "trace_id";
    public static final String DADA_TRACE_ID = "X-WANLI-TRACE-ID";

    /**
     * 将traceId放到MDC中
     * 格式在； trace_id = {uuid}#{层级} 层级：表示当前系统经过了几个系统，比如 A -> B -> C 那么层级分别是0、1、2
     */
    public static String setTraceId(final String beforeTraceId) {
        //把生成的uuid返回
        String uuid;
        String traceId;
        //如果参数为空 或者不合法，则生成traceId
        if (StringUtils.isBlank(beforeTraceId) || StringUtils.contains(beforeTraceId, " ")) {
            uuid = genTraceId();
            traceId = uuid + "#0";
        } else {
            try {
                String[] traces = beforeTraceId.split("#");
                if (traces.length == 1) {
                    //nginx生成的traceId不带#0,所以在这里加上
                    uuid = beforeTraceId;
                    traceId = uuid + "#0";
                } else if (traces.length == 2) {
                    int id = Integer.parseInt(traces[1]) + 1;
                    uuid = traces[0];
                    traceId = uuid + "#" + id;
                } else {
                    //理论上不会到这里
                    uuid = beforeTraceId;
                    traceId = beforeTraceId;
                }
            } catch (Exception e) {
                uuid = beforeTraceId;
                traceId = beforeTraceId;
                logger.error("parse trace id error {}", beforeTraceId, e);
            }
        }
        MDC.put(TRACE_ID, traceId);
        return uuid;
    }

    /**
     * 获取traceId
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 移除traceId
     */
    public static void removeTraceId() {
        MDC.remove(TRACE_ID);
    }

    private static String genTraceId() {
        UUID uuid = UUID.randomUUID();
        return Long.toHexString(uuid.getMostSignificantBits()) + Long.toHexString(uuid.getLeastSignificantBits());
    }


}