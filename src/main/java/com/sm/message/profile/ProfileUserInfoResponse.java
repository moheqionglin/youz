package com.sm.message.profile;

import com.sm.message.admin.AdminCntInfo;
import com.sm.message.order.OrderAllStatusCntInfo;
import com.sm.utils.SmUtil;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-28 17:13
 */
public class ProfileUserInfoResponse {
    private Integer id;
    private String nickName;
    private String headImg;
    private BigDecimal yue;
    private BigDecimal yongjin;
    private String sex;
    private String birthday;
    private String bindYongjinCode;

    private int waitPayCnt;
    private int waitSentCnt;
    private int waitReceiveCnt;
    private int waitCommentCnt;
    private int drawbackCnt;

    private int alertCnt;
    private int orderManagerCnt;
    private int drawbackManagerCnt;
    private int feedManagerCnt;

    public void initOrderCnt(OrderAllStatusCntInfo info) {
        this.setWaitPayCnt(info.getWaitPayCnt());
        this.setWaitSentCnt(info.getWaitSentCnt());
        this.setWaitReceiveCnt(info.getWaitReceiveCnt());
        this.setWaitCommentCnt(info.getWaitCommentCnt());
        this.setDrawbackCnt(info.getDrawbackCnt());
    }

    public void initAdminInfo(AdminCntInfo info) {
        this.setDrawbackManagerCnt(info.getDrawbackManagerCnt());
        this.setOrderManagerCnt(info.getOrderManagerCnt());
        this.setFeedManagerCnt(info.getFeedbackManagerCnt());
    }

    public static class ProfileUserInfoResponseRowMapper implements RowMapper<ProfileUserInfoResponse> {
        @Override
        public ProfileUserInfoResponse mapRow(ResultSet resultSet, int i) throws SQLException {
            ProfileUserInfoResponse user = new ProfileUserInfoResponse();
            if(existsColumn(resultSet, "id")){
                user.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "nick_name")){
                String nickName = resultSet.getString("nick_name");
                user.setNickName(nickName);
            }
            if(existsColumn(resultSet, "head_picture")){
                user.setHeadImg(resultSet.getString("head_picture"));
            }
            if(existsColumn(resultSet, "amount")){
                user.setYue(resultSet.getBigDecimal("amount"));
            }//
            if(existsColumn(resultSet, "yongjin")){
                user.setYongjin(resultSet.getBigDecimal("yongjin"));
            }
            if(existsColumn(resultSet, "birthday")){
                user.setBirthday(SmUtil.parseLongToYMD(resultSet.getDate("birthday").getTime()));
            }
            if(existsColumn(resultSet, "sex")){
                user.setSex(resultSet.getString("sex"));
            }
            if(existsColumn(resultSet, "bind_yongjin_code")){
                user.setBindYongjinCode(resultSet.getString("bind_yongjin_code"));
            }
            return user;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public String getBindYongjinCode() {
        return bindYongjinCode;
    }

    public void setBindYongjinCode(String bindYongjinCode) {
        this.bindYongjinCode = bindYongjinCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public String getSex() {
        return sex;
    }

    public int getFeedManagerCnt() {
        return feedManagerCnt;
    }

    public void setFeedManagerCnt(int feedManagerCnt) {
        this.feedManagerCnt = feedManagerCnt;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getAlertCnt() {
        return alertCnt;
    }

    public void setAlertCnt(int alertCnt) {
        this.alertCnt = alertCnt;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public BigDecimal getYue() {
        return yue;
    }

    public void setYue(BigDecimal yue) {
        this.yue = yue;
    }

    public BigDecimal getYongjin() {
        return yongjin;
    }

    public void setYongjin(BigDecimal yongjin) {
        this.yongjin = yongjin;
    }
    public int getWaitPayCnt() {
        return waitPayCnt;
    }

    public void setWaitPayCnt(int waitPayCnt) {
        this.waitPayCnt = waitPayCnt;
    }

    public int getWaitSentCnt() {
        return waitSentCnt;
    }

    public void setWaitSentCnt(int waitSentCnt) {
        this.waitSentCnt = waitSentCnt;
    }

    public int getWaitReceiveCnt() {
        return waitReceiveCnt;
    }

    public void setWaitReceiveCnt(int waitReceiveCnt) {
        this.waitReceiveCnt = waitReceiveCnt;
    }

    public int getWaitCommentCnt() {
        return waitCommentCnt;
    }

    public void setWaitCommentCnt(int waitCommentCnt) {
        this.waitCommentCnt = waitCommentCnt;
    }

    public int getDrawbackCnt() {
        return drawbackCnt;
    }

    public void setDrawbackCnt(int drawbackCnt) {
        this.drawbackCnt = drawbackCnt;
    }

    public int getOrderManagerCnt() {
        return orderManagerCnt;
    }

    public void setOrderManagerCnt(int orderManagerCnt) {
        this.orderManagerCnt = orderManagerCnt;
    }

    public int getDrawbackManagerCnt() {
        return drawbackManagerCnt;
    }

    public void setDrawbackManagerCnt(int drawbackManagerCnt) {
        this.drawbackManagerCnt = drawbackManagerCnt;
    }
}