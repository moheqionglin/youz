package com.sm.message.wxmessage;

import javax.validation.constraints.NotNull;

public class WxMessage {
    ////发送的openid
    @NotNull
    private String FromUserName;
    //小程序原始id
    private String ToUserName;
    //用户发送客服信息的类型
    private String MsgType;

}
