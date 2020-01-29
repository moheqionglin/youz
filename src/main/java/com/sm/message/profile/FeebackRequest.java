package com.sm.message.profile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-29 20:46
 */
@Valid
public class FeebackRequest {
    @NotNull
    private String content;
    @NotNull
    private String phone;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}