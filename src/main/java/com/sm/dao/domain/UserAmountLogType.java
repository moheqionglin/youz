package com.sm.dao.domain;

public enum UserAmountLogType{
    YUE("YUE"),
    YONGJIN("YONGJIN");

    private final String value;
    private UserAmountLogType(String value) {
        this.value = value;
    }

    public static boolean valid(String type){
        for (UserAmountLogType value : values()) {
            if (value.toString().equals(type)) {
                return true;
            }
        }
        return false;
    }

}