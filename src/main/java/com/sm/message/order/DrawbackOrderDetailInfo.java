package com.sm.message.order;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 10:48
 */
public class DrawbackOrderDetailInfo {

    private Integer orderId;
    private String type;
    private String reason;
    private String detail;
    private List<String> images = new ArrayList<>();

    private String drawbackType;
    private BigDecimal drawbackPayPrice;
    private BigDecimal drawbackYue;
    private BigDecimal drawbackYongjin;

    private Integer approveUserId;
    private String approveComment;

    public static class DrawbackOrderDetailInfoRowMapper implements RowMapper<DrawbackOrderDetailInfo> {

        @Override
        public DrawbackOrderDetailInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            DrawbackOrderDetailInfo dod = new DrawbackOrderDetailInfo();
            if(existsColumn(resultSet, "order_id")){
                dod.setOrderId(resultSet.getInt("order_id"));
            }
            if(existsColumn(resultSet, "drawback_type")){
                dod.setType(resultSet.getString("drawback_type"));
            }
            if(existsColumn(resultSet, "drawback_reason")){
                dod.setReason(resultSet.getString("drawback_reason"));
            }
            if(existsColumn(resultSet, "drawback_detail")){
                dod.setDetail(resultSet.getString("drawback_detail"));
            }
            if(existsColumn(resultSet, "drawback_pay_price")){
                dod.setDrawbackPayPrice(resultSet.getBigDecimal("drawback_pay_price"));
            }
            if(existsColumn(resultSet, "drawback_yue")){
                dod.setDrawbackYue(resultSet.getBigDecimal("drawback_yue"));
            }
            if(existsColumn(resultSet, "drawback_yongjin")){
                dod.setDrawbackYongjin(resultSet.getBigDecimal("drawback_yongjin"));
            }
            if(existsColumn(resultSet, "drawback_imgs")){
                String imgs = resultSet.getString("drawback_imgs");
                if(StringUtils.isNoneBlank(imgs)){
                    dod.setImages(Arrays.stream(imgs.split("|")).collect(Collectors.toList()));
                }
            }
            if(existsColumn(resultSet, "approve_user_id")){
                dod.setApproveUserId(resultSet.getInt("approve_user_id"));
            }
            if(existsColumn(resultSet, "approve_comment")){
                dod.setApproveComment(resultSet.getString("approve_comment"));
            }
            return dod;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getDrawbackType() {
        return drawbackType;
    }

    public void setDrawbackType(String drawbackType) {
        this.drawbackType = drawbackType;
    }

    public BigDecimal getDrawbackPayPrice() {
        return drawbackPayPrice;
    }

    public void setDrawbackPayPrice(BigDecimal drawbackPayPrice) {
        this.drawbackPayPrice = drawbackPayPrice;
    }

    public BigDecimal getDrawbackYue() {
        return drawbackYue;
    }

    public void setDrawbackYue(BigDecimal drawbackYue) {
        this.drawbackYue = drawbackYue;
    }

    public BigDecimal getDrawbackYongjin() {
        return drawbackYongjin;
    }

    public void setDrawbackYongjin(BigDecimal drawbackYongjin) {
        this.drawbackYongjin = drawbackYongjin;
    }

    public Integer getApproveUserId() {
        return approveUserId;
    }

    public void setApproveUserId(Integer approveUserId) {
        this.approveUserId = approveUserId;
    }

    public String getApproveComment() {
        return approveComment;
    }

    public void setApproveComment(String approveComment) {
        this.approveComment = approveComment;
    }
}