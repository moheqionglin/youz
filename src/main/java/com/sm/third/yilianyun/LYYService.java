package com.sm.third.yilianyun;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 易联云接口工具类
 */
public class LYYService {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 易联云颁发给开发者的应用ID 非空值
     */
    @Value("${YLY.c.i}")
    public String CLIENT_ID;

    /**
     * 易联云颁发给开发者的应用secret 非空值
     */
    @Value("${YLY.c.s}")
    public String CLIENT_SECRET;

    @Value("${YLY.m.i}")
    public String mochineCode;
    @Value("${YLY.m.s}")
    public String mochineSec;
    /**
     * token token = "1c55fd95920649f2820253c28daeb412"
     * refresh_token = "354844a1fec54c4b9e8da16508a00352"
     */
    @Value("${YLY.t:}")
    public String token;

    /**
     * 刷新token需要的 refreshtoken f25ed1d0b3bd4dd0b5577c6d0781b9d1
     */
    @Value("${YLY.r.t:}")
    public String refresh_token;


    /**
     * code
     */
    public String CODE;

    @PostConstruct
    public void doInit(){
        try{
            if(StringUtils.isBlank(token) || StringUtils.isBlank(refresh_token)){
                this.getFreedomToken();
            }
//            this.refreshToken();
            this.addPrinter(mochineCode, mochineSec);
        }catch (Exception e){
            log.error("init printer error", e);
        }


    }
    /**
     * 开放式初始化
     *
     * @param client_id
     * @param client_secret
     * @param code
     */
    public void init(String client_id, String client_secret, String code) {
        CLIENT_ID = client_id;
        CLIENT_SECRET = client_secret;
        CODE = code;
    }

    /**
     * 自有初始化
     *
     * @param client_id
     * @param client_secret
     */

    /**
     * 开放应用
     */
    public String getToken() {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String result = LAVApi.getToken(CLIENT_ID,
                "authorization_code",
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                CODE,
                "all",
                timestamp,
                LAVApi.getuuid());
        try {
            JSONObject json = JSONObject.parseObject(result);
            JSONObject body = json.getJSONObject("body");
            token = body.getString("access_token");
            refresh_token = body.getString("refresh_token");
        } catch (JSONException e) {
            log.error("getToken出现Json异常！" + e);
        }
        return result;
    }

    /**
     * 自有应用服务
     */

    public String getFreedomToken() {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String result = LAVApi.getToken(CLIENT_ID,
                "client_credentials",
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                "all",
                timestamp,
                LAVApi.getuuid());
        try {
            JSONObject json = JSONObject.parseObject(result);
            JSONObject body = json.getJSONObject("body");
            token = body.getString("access_token");
            refresh_token = body.getString("refresh_token");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("getFreedomToken出现Json异常！" + e);
        }
        return result;
    }

    /**
     * 刷新token
     */
    public String refreshToken() {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String result = LAVApi.refreshToken(CLIENT_ID,
                "refresh_token",
                "all",
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                refresh_token,
                LAVApi.getuuid(),
                timestamp);
        try {
            JSONObject json =JSONObject.parseObject(result);
            JSONObject body = json.getJSONObject("body");
            token = body.getString("access_token");
            refresh_token = body.getString("refresh_token");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("refreshToken出现Json异常！" + e);
        }
        return result;
    }

    /**
     * 添加终端授权 开放应用服务模式不需要此接口 ,自有应用服务模式所需参数
     */
    public String addPrinter(String machine_code, String msign) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.addPrinter(CLIENT_ID,
                machine_code,
                msign,
                token,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 极速授权
     */
    public String speedAu(String machine_code, String qr_key) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.speedAu(CLIENT_ID,
                machine_code,
                qr_key,
                "all",
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 打印
     */
    public String print(String machine_code, String content, String origin_id) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.print(CLIENT_ID,
                token,
                machine_code,
                content,
                origin_id,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 删除终端授权
     */
    public String deletePrinter(String machine_code) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.deletePrinter(CLIENT_ID,
                token,
                machine_code,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 添加应用菜单
     */
    public String addPrintMenu(String machine_code, String content) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.addPrintMenu(CLIENT_ID,
                token,
                machine_code,
                content,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 关机重启接口
     */
    public String shutDownRestart(String machine_code, String response_type) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.shutDownRestart(CLIENT_ID,
                token,
                machine_code,
                response_type,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 声音调节
     */
    public String setSound(String machine_code, String response_type, String voice) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.setSound(CLIENT_ID,
                token,
                machine_code,
                response_type,
                voice,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 获取机型打印宽度接口
     */
    public String getPrintInfo(String machine_code) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.getPrintInfo(CLIENT_ID,
                token,
                machine_code,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 获取机型软硬件版本接口
     */
    public String getVersion(String machine_code) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.getVersion(CLIENT_ID,
                token,
                machine_code,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 取消所有未打印订单
     */
    public String cancelAll(String machine_code) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.cancelAll(CLIENT_ID,
                token,
                machine_code,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 取消单条未打印订单
     */
    public String cancelOne(String machine_code, String order_id) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.cancelOne(CLIENT_ID,
                token,
                machine_code,
                order_id,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 设置logo
     */
    public String setIcon(String machine_code, String img_url) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.setIcon(CLIENT_ID,
                token,
                machine_code,
                img_url,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 删除logo
     */
    public String deleteIcon(String machine_code) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.deleteIcon(CLIENT_ID,
                token,
                machine_code,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 打印方式
     */
    public String btnPrint(String machine_code, String response_type) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.btnPrint(CLIENT_ID,
                token,
                machine_code,
                response_type,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

    /**
     * 接单拒单设置接口
     */
    public String getOrder(String machine_code, String response_type) {
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        return LAVApi.getOrder(CLIENT_ID,
                token,
                machine_code,
                response_type,
                LAVApi.getSin(timestamp, CLIENT_ID, CLIENT_SECRET),
                LAVApi.getuuid(),
                timestamp);
    }

}