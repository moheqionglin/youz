package com.example.auth.demo.common;

import com.qiniu.common.Zone;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
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
        String accessKey = "amtEOsSrfMORu128wsKxhjUPJfaZlptyP3SbYt97";
        String secretKey = "jijbGuNnZYO9AAtfh2QDdEeQuDV-F-trRlEIJCfO";
        String bucketName = "suimeikeji";
        String key = "tmp";
        Configuration cfg = new Configuration(Region.region0());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);

        StringMap policy = new StringMap();
        policy.put("insertOnly", 1);
        String token = auth.uploadToken(bucketName, null, 3600 * 2, policy, true);
        System.out.println(token);
//        Response r = upManager.put("hello world".getBytes(), "yourkey", token);
    }
}