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
import org.springframework.web.bind.annotation.*;


@RestController
@Api(tags={"message"})
@RequestMapping("/v1/message/")
public class MessageController {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WXBizMsgCrypt wxBizMsgCrypt;
    @GetMapping
    @ApiOperation(value = "[验证消息] ")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signature", value = "参数\t描述", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "timestamp", value = "微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "nonce", value = "时间戳", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "echostr", value = "随机字符串", required = true, paramType = "query", dataType = "String"),

    })
    public String receive(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
           @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr){

        if(!checkFromWx(signature, echostr, timestamp, nonce)){
            log.warn("signature = {}, timestamp = {}, nonce = {}, echostr = {}",signature , timestamp, nonce, echostr);
            return "ERROR";
        }
        log.info("[receive success from wx] {}", echostr);
        return "success";
    }

    @PostMapping
    @ApiOperation(value = "[接受消息] ")
    public String receiveWx(@RequestParam("signature") String signature, @RequestParam("timestamp") String timestamp,
                         @RequestParam("nonce") String nonce, @RequestParam("echostr") String echostr){
        if(!checkFromWx(signature, echostr, timestamp, nonce)){
            log.warn("signature = {}, timestamp = {}, nonce = {}, echostr = {}",signature , timestamp, nonce, echostr);
            return "ERROR";
        }
        log.info("[receive success from wx] {}", echostr);
        return "success";
    }

    private boolean checkFromWx(String signature, String echostr, String timestamp, String nonce){
        try {
            String sing = wxBizMsgCrypt.encryptMsg(echostr, timestamp, nonce);
            if(sing.equals(signature)){
                return true;
            }
        } catch (AesException e) {
            log.error("", e);
        }
        return false;
    }
}
