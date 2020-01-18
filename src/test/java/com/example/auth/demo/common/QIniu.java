package com.example.auth.demo.common;

import com.qiniu.common.Zone;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.junit.Test;
import com.qiniu.storage.Configuration;
/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-17 10:57
 */
public class QIniu {
    @Test
    public void atest(){
        //https://developer.qiniu.com/kodo/sdk/1283/javascript
        //Region.region0() 华东
        String accessKey = "";
        String secretKey = "";
        String bucketName = "";
        Configuration cfg = new Configuration(Region.region0());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String token = auth.uploadToken(bucketName);
        System.out.println(token);
//        Response r = upManager.put("hello world".getBytes(), "yourkey", token);
    }
}