package com.example.auth.demo;

import com.alibaba.fastjson.JSONObject;
import com.sm.message.WxCode2SessionResponse;
import com.sm.service.AuthServiceImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 13:33
 */
//@Ignore
public class RestTemplateTest extends BaseTest{


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthServiceImpl authService;

//    @Ignore
    @Test
    public void codeTest(){

        String code = "021FWsTx1rLhDb0PMfUx1n2vTx1FWsTi\n";
        WxCode2SessionResponse wxCode2SessionResponse = authService.getWxCode2SessionResponse(code);
        System.out.println(wxCode2SessionResponse);
    }
    @Ignore
    @Test
    public void getTest(){
        final String WX_CODE_2_OPENID_URL = "https://api.weixin.qq.com/sns/jscode2session?appid={appid}&secret={secret}&js_code={js_code}&grant_type={grant_type}";

        JSONObject forObject = restTemplate.getForObject(WX_CODE_2_OPENID_URL, JSONObject.class);
        System.out.println(forObject);
    }
}