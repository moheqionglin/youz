package com.sm.message.comment;

import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 21:47
 */
public class CommentInfo {

    private Integer id;
    private Integer userId;
    private String nickName;
    private String headPic;

    private Integer productId;
    private String productName;
    private String productProfileImg;
    private String productSize;
    private String comment;
    private boolean good;
    private List<String> images;
    private String createDate;

    private AppendCommentInfo appendCommentInfo;
    public static class CommentInfoRowMapper implements RowMapper<CommentInfo> {
        @Override
        public CommentInfo mapRow(ResultSet resultSet, int i) throws SQLException {
            CommentInfo commentInfo = new CommentInfo();

            if(existsColumn(resultSet, "id")){
                commentInfo.setId(resultSet.getInt("id"));
            }
            if(existsColumn(resultSet, "user_id")){
                commentInfo.setUserId(resultSet.getInt("user_id"));
            }
            if(existsColumn(resultSet, "nick_name")){
                String nick_name = resultSet.getString("nick_name");
                if(StringUtils.isNoneBlank(nick_name)){
                    commentInfo.setNickName(SmUtil.mockName(nick_name));
                }
            }
            if(existsColumn(resultSet, "head_picture")){
                commentInfo.setHeadPic(resultSet.getString("head_picture"));
            }
            if(existsColumn(resultSet, "product_id")){
                commentInfo.setProductId(resultSet.getInt("product_id"));
            }
            if(existsColumn(resultSet, "product_name")){
                commentInfo.setProductName(resultSet.getString("product_name"));
            }
            if(existsColumn(resultSet, "product_size")){
                commentInfo.setProductSize(resultSet.getString("product_size"));
            }
            if(existsColumn(resultSet, "product_profile_img")){
                commentInfo.setProductProfileImg(resultSet.getString("product_profile_img"));
            }
            if(existsColumn(resultSet, "good")){
                commentInfo.setGood(resultSet.getBoolean("good"));
            }
            if(existsColumn(resultSet, "comment")){
                commentInfo.setComment(resultSet.getString("comment"));
            }
            if(existsColumn(resultSet, "created_time")){
                commentInfo.setCreateDate(SmUtil.parseLongToTMDHMS(resultSet.getTimestamp("created_time").getTime()));
            }

            if(existsColumn(resultSet, "images")){
                String images = resultSet.getString("images");
                if(StringUtils.isNoneBlank(images)){
                    commentInfo.setImages(Arrays.stream(images.split("\\|")).collect(Collectors.toList()));
                }

            }
            return commentInfo;
        }
        private boolean existsColumn(ResultSet rs, String column) {
            try {
                return rs.findColumn(column) > 0;
            } catch (SQLException sqlex) {
                return false;
            }
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public AppendCommentInfo getAppendCommentInfo() {
        return appendCommentInfo;
    }

    public void setAppendCommentInfo(AppendCommentInfo appendCommentInfo) {
        this.appendCommentInfo = appendCommentInfo;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductProfileImg() {
        return productProfileImg;
    }

    public void setProductProfileImg(String productProfileImg) {
        this.productProfileImg = productProfileImg;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isGood() {
        return good;
    }

    public void setGood(boolean good) {
        this.good = good;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}