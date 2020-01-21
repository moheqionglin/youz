package com.sm.controller;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 21:31
 */
public enum  HttpYzCode {
    SUCCESS(200, "成功"),

    BAD_REQUEST(400, "参数或者语法不对"),
    UNAUTHORIZED(401, "认证失败"),
    LOGIN_ERROR(401, "登陆失败，用户名或密码无效"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "请求的资源不存在"),
    OPERATE_ERROR(405, "操作失败，请求操作的资源不存在"),


    SERVER_ERROR(500, "服务器内部错误"),



    CART_CNT_EXCEED_LIMIT(406, "购物车超过30个"),
    EXCEED_STOCK(407, "库存不足"),
    PRODUCT_XIAJIA(408, "产品下架"),
    YONGJIN_YUE_NOT_ENOUGH(409, "余额佣金不足"),
    ADDRESS_NOT_EXISTS(410, "地址不存在"),

    ORDER_NOT_EXISTS(420, "订单不存在"),
    ORDER_STATUS_ERROR(421, "订单状态不对"),
    ORDER_DRAWBACK_REPEAT_ERROR(422, "订单状态不对"),
    ORDER_JIANHUO_REPEAT(430, "订单已经被其他检货员认领"),

    ORDER_NO_JIANHUO(431, "订单没有检货员认领"),
    ORDER_JIANHUO_NOT_CURRENT_ORDER(432, "自己不是该订单的拣货员"),

    CATEGORY_HAS_CHILD(440, "分类下面有资源");
    private int code;
    private String messsage;

    HttpYzCode(int code, String messsage) {
        this.code = code;
        this.messsage = messsage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMesssage() {
        return messsage;
    }

    public void setMesssage(String messsage) {
        this.messsage = messsage;
    }
}