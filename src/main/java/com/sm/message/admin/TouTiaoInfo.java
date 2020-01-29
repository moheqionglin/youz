package com.sm.message.admin;

import com.sm.dao.domain.TouTiao;
import com.sm.utils.SmUtil;

import javax.validation.constraints.NotNull;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-29 13:27
 */
public class TouTiaoInfo {
    private Integer id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    private String createdTime;


    public TouTiaoInfo() {
    }

    public TouTiaoInfo(TouTiao t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.content = t.getContent();
        this.createdTime = SmUtil.parseLongToYMD(t.getCreatedTime().getTime());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }
}