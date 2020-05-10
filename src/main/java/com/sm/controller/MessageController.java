package com.sm.controller;

import com.sm.utils.wx.AesException;
import com.sm.utils.wx.WXBizMsgCrypt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


@RestController
@Api(tags={"message"})
@RequestMapping("/api/v1/message")
public class MessageController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WXBizMsgCrypt wxBizMsgCrypt;
    @Value("${messageTk}")
    private String messageTk;
    @Value("${messageK}")
    private String messageK;

    @GetMapping(path = "/wx")
    @ApiOperation(value = "[验证消息] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signature", value = "参数\t描述", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "timestamp", value = "微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "nonce", value = "时间戳", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "echostr", value = "随机字符串", required = true, paramType = "query", dataType = "String"),

    })
    public String receive(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
           @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr){

        if(!checkSignature(signature, timestamp, nonce)){
            log.warn("signature = {}, timestamp = {}, nonce = {}, echostr = {}",signature , timestamp, nonce, echostr);
            return "ERROR";
        }
        log.info("[receive success from wx] {}", echostr);
        return echostr;
    }

    @PostMapping(path = "/wx")
    @ApiOperation(value = "[接受消息] ")
    public String receiveWx(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                         @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr){
        if(!checkSignature(signature, timestamp, nonce)){
            log.warn("signature = {}, timestamp = {}, nonce = {}, echostr = {}",signature , timestamp, nonce, echostr);
            return "ERROR";
        }
        log.info("[receive success from wx] {}", echostr);
        return "success";
    }

    public boolean checkSignature(String signature, String timestamp,
                                         String nonce) {

        // 将token、timestamp、nonce三个参数进行字典排序  
        String[] arr = new String[] { messageTk, timestamp, nonce };
        Arrays.sort(arr);

        // 将三个参数字符串拼接成一个字符串
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        try {
            //获取加密工具
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            // 对拼接好的字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            String tmpStr = byteToStr(digest);
            //获得加密后的字符串与signature对比
            return tmpStr != null ? tmpStr.equals(signature.toUpperCase()): false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }
}
