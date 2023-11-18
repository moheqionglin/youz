package com.sm.message.tuangou;

import com.sm.message.order.OrderDetailItemInfo;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class TuangouOrderInfo {
    private static final SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd");
    private String nickName;
    private String img;
    private BigDecimal tuangouDrawbackAmount;
    private BigDecimal displayTuangouDrawbackAmount;
    private String date;
    private Integer orderId;
    private Integer tuangouId;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public BigDecimal getTuangouDrawbackAmount() {
        return tuangouDrawbackAmount;
    }

    public Integer getTuangouId() {
        return tuangouId;
    }

    public void setTuangouId(Integer tuangouId) {
        this.tuangouId = tuangouId;
    }

    public void setTuangouDrawbackAmount(BigDecimal tuangouDrawbackAmount) {
        this.tuangouDrawbackAmount = tuangouDrawbackAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public static class TuangouOrderDetailItemInfoRowMapper implements RowMapper<TuangouOrderInfo> {

        @Override
        public TuangouOrderInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            TuangouOrderInfo item = new TuangouOrderInfo();
            if(existsColumn(resultSet, "id")){
                item.setOrderId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "tuangou_drawback_amount")){
                item.setTuangouDrawbackAmount(resultSet.getBigDecimal("tuangou_drawback_amount"));
                item.setDisplayTuangouDrawbackAmount(item.getTuangouDrawbackAmount().abs());
            }
            if(existsColumn(resultSet, "nick_name")){
                item.setNickName(resultSet.getString("nick_name"));
            }
            if(existsColumn(resultSet, "head_picture")){
                item.setImg(resultSet.getString("head_picture"));
            }
            if(existsColumn(resultSet, "tuangou_id")){
                item.setTuangouId(resultSet.getInt("tuangou_id"));
            }
            if(existsColumn(resultSet, "created_time")){
                item.setDate(simpleDateFormat.format(resultSet.getDate("created_time")));
            }
            return item;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public BigDecimal getDisplayTuangouDrawbackAmount() {
        return displayTuangouDrawbackAmount;
    }

    public void setDisplayTuangouDrawbackAmount(BigDecimal displayTuangouDrawbackAmount) {
        this.displayTuangouDrawbackAmount = displayTuangouDrawbackAmount;
    }

}
