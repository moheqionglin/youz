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
    YONGJIN_YUE_PRICE_ERROR(411, "佣金余额错误"),

    ORDER_NOT_EXISTS(420, "订单不存在"),
    ORDER_STATUS_ERROR(421, "订单状态不对"),
    ORDER_DRAWBACK_REPEAT_ERROR(422, "订单状态不对"),
    ORDER_JIANHUO_REPEAT(430, "订单已经被其他检货员认领"),
    ORDER_CHAJIA_WAIT_PAY(423, "有未支付的差价订单"),

    ORDER_NO_JIANHUO(431, "订单没有检货员认领"),
    ORDER_JIANHUO_NOT_CURRENT_ORDER(432, "自己不是该订单的拣货员"),

    CATEGORY_HAS_CHILD(440, "分类下面有资源"),

    TIXIAN_AMOUNT_LESS(450, "提现金额小于1"),
    TIXIAN_AMOUNT_EXCEED(451, "提现金额超过实际余额"),
    YONGJIN_BILI_TOO_MAX(460, "佣金比例应该小于1"),
    YONGJIN_CODE_IS_SELF(461, "佣金码不能填写自己的"),

    TIXIAN_ERROR(452, "提现错误"),
    KANJIA_HELP_OTHER_EXISTS(470, "已经帮助别人砍过价"),
    KANJIA_SELF_EXISTS(471, "已经发起过砍价"),
    PRODUCT_NOT_EXISTS(472, "产品不存在"),
    PRODUCT_NOT_KANJIA(473, "产品不是砍价商品"),
    PRODUCT_PRICE_ERROR(474, "砍价商品价格错误"),
    PRODUCT_CART_EXISTS_KANJIA(475, "购物车中已经有了砍价商品"),

    SHOUYIN_PAY_ERROR(485, "支付失败"),
    SHOUYIN_ORDER_STATUS_ERROR(486, "收银订单状态不是待支付"),
    SHOUYIN_HAD_KAIGONG(487, "已经开工，请勿重复开工"),
    SHOUYIN_NO_KAIGONG(488, "还没有开工，不能点击手工。"),
    ;
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