package com.sm.message.comment;

import com.sm.message.order.SimpleOrderItem;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 22:21
 */
public class AppendCommentInfo {

    private Integer productCommentId;
    private String comment;
    private List<String> images = new ArrayList<>(1);
    private boolean good;
    private String createdDate;

    public static class AppendCommentInfoRowMapper implements RowMapper<AppendCommentInfo> {
        @Override
        public AppendCommentInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            AppendCommentInfo appendCommentInfo = new AppendCommentInfo();
            if(existsColumn(resultSet, "product_comment_id")){
                appendCommentInfo.setProductCommentId(resultSet.getInt("product_comment_id"));
            }
            if(existsColumn(resultSet, "good")){
                appendCommentInfo.setGood(resultSet.getBoolean("good"));
            }
            if(existsColumn(resultSet, "comment")){
                appendCommentInfo.setComment(resultSet.getString("comment"));
            }
            if(existsColumn(resultSet, "created_time")){
                appendCommentInfo.setCreatedDate(SmUtil.parseLongToTMDHMS(resultSet.getTimestamp("created_time").getTime()));
            }
            if(existsColumn(resultSet, "images")){
                String images = resultSet.getString("images");
                if(StringUtils.isNoneBlank(images)){
                    appendCommentInfo.setImages(Arrays.stream(images.split("\\|")).collect(Collectors.toList()));
                }

            }
            return appendCommentInfo;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }

    public Integer getProductCommentId() {
        return productCommentId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setProductCommentId(Integer productCommentId) {
        this.productCommentId = productCommentId;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}