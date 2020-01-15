package com.sm.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 23:12
 */
public class SmUtil {
    public static String mockName(String name){
        if(StringUtils.isBlank(name)){
            return name;
        }
        name = StringUtils.trimToEmpty(name);
        if(name.length() <= 1){
            return name;
        }
        char startChar = name.charAt(0);
        if(name.length() == 2){
            return startChar + "*";
        }
        char endChar = name.charAt(name.length() - 1);
        return startChar + "**" + endChar;

    }

    //订单编号前缀
    public static final String PREFIX = "P";
    //订单编号后缀（核心部分）
    private static long code;

    // 生成订单编号
    public static synchronized String getOrderCode() {
        code++;
        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long m = Long.parseLong((str)) * 10000;
        m += code;
        return PREFIX + m;
    }

}