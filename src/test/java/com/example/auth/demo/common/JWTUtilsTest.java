package com.example.auth.demo.common;

import com.example.auth.demo.BaseTest;
import com.sm.utils.JwtUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName : JWTUtilsTest
 * @Description :
 * @Author : wanli.zhou
 * @Date: 2020-02-29 16:14
 */
public class JWTUtilsTest extends BaseTest {
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void getUserIdFromTokenTest(){
        String to = "eyJhbGciOiJIUzI1NiIsInppcCI6IkRFRiJ9.eNqqViouTVKyUnq6aMWL3u3PJ-x5Nm-6ko5SfjJQLN-z3D_P1M03p9jF19HR0N8v1Dm-IqoiNNNRtxioprQ4tSg-M0XJytDQQkepKD8ntRioKTpGySk00jUoRkknRsnL09HPI9QfzHZ08fX0i1GKBepMrSgA6jK1MDK3NLC0NNNRykwsAQsYWhqZgQWySjKBZiUnGxkYp6QY6BomGRnrmhinJOtapiWn6JoZmRkZmKYYmSYbJivVAgAAAP__.Ducl5ODvNBPBNMY3364bGP5IqFvqgsF-gKhzmm4kAj8";
        int userIdFromToken = jwtUtils.getUserIdFromToken(to);

    }
}
