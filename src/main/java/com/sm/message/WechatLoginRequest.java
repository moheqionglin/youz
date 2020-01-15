package com.sm.message;

import io.swagger.annotations.ApiModelProperty;

public class WechatLoginRequest {
    @ApiModelProperty(value = "微信code", required = true)
    private String code;
    @ApiModelProperty(value = "用户非敏感字段")
    private String rawData;
    @ApiModelProperty(value = "签名")
    private String signature;
    @ApiModelProperty(value = "用户敏感字段")
    private String encryptedData;
    @ApiModelProperty(value = "解密向量")
    private String iv;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "WechatLoginRequest{" +
                "code='" + code + '\'' +
                ", rawData='" + rawData + '\'' +
                ", signature='" + signature + '\'' +
                ", encryptedData='" + encryptedData + '\'' +
                ", iv='" + iv + '\'' +
                '}';
    }
}