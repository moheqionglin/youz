package com.sm.message.profile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 19:40
 */
public class UpdateProfileRequest implements Serializable {
    @NotNull
    private String nickName;
    @NotNull
    private String sex;
    @NotNull
    private String headPicture;
    @NotNull
    private String birthday;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }
}