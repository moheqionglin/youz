package com.sm.message.product;

import com.sm.message.profile.UserSimpleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:49
 */
public class KanjiaDetailInfo {
    private Integer userId;
    private Integer productId;
    private boolean terminal;
    List<UserSimpleInfo> kanjieHelpers = new ArrayList<>(1);

    public Integer getUserId() {
        return userId;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public List<UserSimpleInfo> getKanjieHelpers() {
        return kanjieHelpers;
    }

    public void setKanjieHelpers(List<UserSimpleInfo> kanjieHelpers) {
        this.kanjieHelpers = kanjieHelpers;
    }
}