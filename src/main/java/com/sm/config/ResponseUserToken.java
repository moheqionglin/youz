package com.sm.config;


import java.util.List;

/**
 * createAt: 2018/9/17
 */
public class ResponseUserToken {
    private String token;
    private SimpleU userDetail;

    public ResponseUserToken(String token, SimpleU userDetail) {
        this.token = token;
        this.userDetail = userDetail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SimpleU getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(SimpleU userDetail) {
        this.userDetail = userDetail;
    }

    public static class SimpleU{
        private Integer id;
        private List<String> roles;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }
    }
}
