package com.sm.message.profile;

import com.sm.utils.SmUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 20:10
 */
public class YueItemResponse {
    private String dealDate;
    private String message;
    private BigDecimal amount;

    public YueItemResponse() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public YueItemResponse(Date dealDate, String remark, BigDecimal amount) {
        this.amount = amount;
        this.dealDate = dealDate != null ? SmUtil.parseLongToTMDHMS(dealDate.getTime()): "";
        this.message = remark;
    }

    public String getDealDate() {
        return dealDate;
    }

    public void setDealDate(String dealDate) {
        this.dealDate = dealDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}