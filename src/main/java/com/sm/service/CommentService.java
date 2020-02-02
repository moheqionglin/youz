package com.sm.service;

import com.sm.dao.dao.CommentDao;
import com.sm.message.comment.AppendCommentInfo;
import com.sm.message.comment.AppendCommentRequest;
import com.sm.message.comment.CommentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 21:46
 */
@Component
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    public List<CommentInfo> getMyCommentPaged(int userID, int pageSize, int pageNum) {
        List<CommentInfo> myCommentPaged = commentDao.getCommentPaged(userID, pageSize, pageNum);
        fillAppendComment(myCommentPaged);
        return myCommentPaged;
    }

    public List<CommentInfo> getProductCommentPaged(int pid, int pageSize, int pageNum) {
        List<CommentInfo> myCommentPaged = commentDao.getProductCommentPaged(pid, pageSize, pageNum);
        fillAppendComment(myCommentPaged);
        return myCommentPaged;
    }

    private void fillAppendComment(List<CommentInfo> myCommentPaged) {
        List<Integer> ids = myCommentPaged.stream().map(m -> m.getId()).collect(Collectors.toList());
        if(ids.isEmpty()){
            return ;
        }
        List<AppendCommentInfo> appendComment = commentDao.getAppendComment(ids);
        Map<Integer, List<AppendCommentInfo>> appcomms = appendComment.stream().collect(Collectors.groupingBy(AppendCommentInfo::getProductCommentId));
        myCommentPaged.stream().forEach(c -> {
            if(appcomms.get(c.getId()) != null) {
                c.setAppendCommentInfo(appcomms.get(c.getId()).stream().findFirst().orElse(null));
            }
        });
    }

    public void appendComment(int userID, String commentId, AppendCommentRequest appendCommentRequest) {
        appendCommentRequest.setUserId(userID);
       commentDao.appendComment(commentId, appendCommentRequest);
    }
}