package com.sm.dao.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-07 19:25
 */
public class User {
    private Integer id;
    private String sex;
    private String password;
    private String nickName;
    private Date birthday;
    private Timestamp regTime;
    private String headPicture;
    private boolean disable;
    private String openCode;
    private Timestamp lastLogin;
    private BigDecimal amount;
    private BigDecimal yongjin;
    private Timestamp createdTime;
    private Timestamp modifiedTime;

    public User() {
    }

    public User(Integer id, String sex, String password, String nickName, Date birthday, Timestamp regTime, String headPicture, boolean disable, String openCode, Timestamp lastLogin, BigDecimal amount, BigDecimal yongjin, Timestamp createdTime, Timestamp modifiedTime) {
        this.id = id;
        this.sex = sex;
        this.password = password;
        this.nickName = nickName;
        this.birthday = birthday;
        this.regTime = regTime;
        this.headPicture = headPicture;
        this.disable = disable;
        this.openCode = openCode;
        this.lastLogin = lastLogin;
        this.amount = amount;
        this.yongjin = yongjin;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Timestamp getRegTime() {
        return regTime;
    }

    public void setRegTime(Timestamp regTime) {
        this.regTime = regTime;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getOpenCode() {
        return openCode;
    }

    public void setOpenCode(String openCode) {
        this.openCode = openCode;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getYongjin() {
        return yongjin;
    }

    public void setYongjin(BigDecimal yongjin) {
        this.yongjin = yongjin;
    }

    public Timestamp getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Timestamp createdTime) {
        this.createdTime = createdTime;
    }

    public Timestamp getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Timestamp modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}