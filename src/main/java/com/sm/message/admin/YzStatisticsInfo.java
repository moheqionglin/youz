package com.sm.message.admin;

import com.sm.message.order.SimpleOrderItem;
import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 20:37
 */
public class YzStatisticsInfo {
    YzStatisticsInfoItem total  = new YzStatisticsInfoItem();
    YzStatisticsInfoItem online = new YzStatisticsInfoItem();
    YzStatisticsInfoItem offline = new YzStatisticsInfoItem();

    public void initTotal(){
        if(online != null && offline != null){
            total.setDate(online.getDate());
            total.setDatelong(online.getDatelong());
            total.setTotalCnt(online.getTotalCnt().add(offline.getTotalCnt()));
            total.setTotalPrice(online.getTotalPrice().add(offline.getTotalPrice()));
            total.setTotalCost(online.getTotalCost().add(offline.getTotalCost()));
            total.setTotalProfit(online.getTotalProfit().add(offline.getTotalProfit()));
        }else if(online != null){
            total = online;
        }else if(offline != null){
            total = offline;
        }
    }
    public YzStatisticsInfoItem getTotal() {
        return total;
    }

    public void setTotal(YzStatisticsInfoItem total) {
        this.total = total;
    }

    public YzStatisticsInfoItem getOnline() {
        return online;
    }

    public void setOnline(YzStatisticsInfoItem online) {
        this.online = online;
    }

    public YzStatisticsInfoItem getOffline() {
        return offline;
    }

    public void setOffline(YzStatisticsInfoItem offline) {
        this.offline = offline;
    }
}
