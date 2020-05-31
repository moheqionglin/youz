package com.sm.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 23:12
 */
public class SmUtil {
    public static DateTimeFormatter ymr = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter ymrhms = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static Random random = new Random();

    public static String parseLongToYMD(long time){
        return ymr.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault()));
    }

    public static String parseLongToTMDHMS(long time){
        return ymrhms.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(time),ZoneId.systemDefault()));
    }

    public static String getTodayYMD(){
        return ymr.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(new Date().getTime()), ZoneId.systemDefault()));
    }
    public static Object getLastTodayYMD() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(new Date().getTime()), ZoneId.systemDefault());
        return ymr.format(localDateTime.minusDays(1));
    }
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
    //线下收银编号
    public static final String PREFIX_SY = "SYP";
    public static final String PREFIX_SYD = "SYD";
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
    // 生成订单编号
    public static synchronized String getShouYinCode(boolean isDrawback) {
        code++;
        String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long m = Long.parseLong((str)) * 10000;
        m += code;
        return (isDrawback? PREFIX_SYD : PREFIX_SY) + m;
    }
    public static long getLongTimeFromYMDHMS(String start) {
        LocalDateTime localDateTime = LocalDateTime.parse(start, ymrhms);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant).getTime();
    }

    /**
     * 当前年月日时分秒 + 随机数 转成十六进制
     * @return
     */
    public static String generageYongjinCode() {
        String current = Long.toString(System.currentTimeMillis(), 32);
        String ran = Integer.toHexString(random.nextInt(1000));
        return StringUtils.upperCase(current+"-"+ran);
    }
}