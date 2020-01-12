package com.sm.message.wechat;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 21:19
 */
public class WxCode2SessionResponse {

    private String session_key;
    private String openid;
    private int expires_in;

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }
}