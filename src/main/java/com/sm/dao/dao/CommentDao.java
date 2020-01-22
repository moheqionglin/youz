package com.sm.dao.dao;

import com.sm.message.comment.AppendCommentInfo;
import com.sm.message.comment.AppendCommentRequest;
import com.sm.message.comment.CommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 21:47
 */
@Component
public class CommentDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<CommentInfo> getCommentPaged(int userID, int pageSize, int pageNum) {
        final String sql = String.format("select t1.id as id ,user_id,product_id,product_name,product_profile_img,product_size,good,comment,t1.images as images,t1.created_time as created_time, t2.nick_name, t2.head_picture from %s t1 left join %s t2 on t1.user_id = t2.id where user_id = ? order by t1.id desc limit ?, ?" , VarProperties.PRODUCT_COMMENT);
        int startIndex = (pageNum - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{userID, startIndex, pageSize}, new CommentInfo.CommentInfoRowMapper());
    }

    public List<AppendCommentInfo> getAppendComment(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return new ArrayList<>(1);
        }
        final String sql = String.format("select  product_comment_id , good,comment , images ,created_time from %s where product_comment_id in (:ids)", VarProperties.PRODUCT_APPEND_COMMENT);
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", ids), new AppendCommentInfo.AppendCommentInfoRowMapper());
    }

    public List<CommentInfo> getProductCommentPaged(int pid, int pageSize, int pageNum) {
        final String sql = String.format("select t1.id as id ,user_id,product_id,product_name,product_profile_img,product_size,good,comment,t1.images as images,t1.created_time as created_time, t2.nick_name, t2.head_picture from %s t1 left join %s t2 on t1.user_id = t2.id where product_id = ? order by t1.id desc limit ?, ?" , VarProperties.PRODUCT_COMMENT, VarProperties.USERS);
        int startIndex = (pageNum - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{pid, startIndex, pageSize}, new CommentInfo.CommentInfoRowMapper());
    }


    public void appendComment(String commentId, AppendCommentRequest append) {
        final String sql = String.format("insert into %s ( product_comment_id,user_id,good,comment,images) values (?,?,?,?,?)", VarProperties.PRODUCT_APPEND_COMMENT);
        String imgs = append.getImages() == null ? "" : append.getImages().stream().collect(Collectors.joining("|"));
        jdbcTemplate.update(sql, new Object[]{commentId, append.getUserId(), append.isGood(), append.getComment(), imgs});
    }
}