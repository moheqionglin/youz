package com.sm.dao.domain;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:24
 */
public class UserToken {
    private int id;
    private String token;
    private int userId;

    public UserToken() {
    }

    public UserToken(String token, int userId) {
        this.token = token;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}