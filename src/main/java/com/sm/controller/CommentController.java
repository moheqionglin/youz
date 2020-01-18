package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.comment.AppendCommentRequest;
import com.sm.message.comment.CommentInfo;
import com.sm.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-15 21:36
 */
@RestController
@Api(tags={"comment"})
@RequestMapping("/api/v1/")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping(path = "/comment/my/list")
    @PreAuthorize("hasAuthority('BUYER')")
    @ApiOperation(value = "[获取自己的评价列表]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<CommentInfo> getMyCommentPaged(@Valid @NotNull @RequestParam("page_size") int pageSize,
                                                 @Valid @NotNull @RequestParam("page_num") int pageNum){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        return commentService.getMyCommentPaged(userDetail.getId(), pageSize, pageNum);
    }

    @GetMapping(path = "/comment/product/{pid}/list")
    @ApiOperation(value = "[获取产品评价列表]")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pid", value = "pid", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "page_size", value = "page_size", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "page_num", value = "page_num", required = true, paramType = "query", dataType = "Integer")
    })
    public List<CommentInfo> getProductCommentPaged(@Valid @NotNull @PathVariable("pid") int pid,
                                                    @Valid @NotNull @RequestParam("page_size") int pageSize,
                                                    @Valid @NotNull @RequestParam("page_num") int pageNum){

        return commentService.getProductCommentPaged(pid, pageSize, pageNum);
    }

    @PostMapping(path = "/comment/append/{id}")
    @PreAuthorize("hasAuthority('BUYER') ")
    @ApiOperation(value = "[评论追加商品] id是 commentid")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "append", value = "append", required = true, paramType = "body", dataType = "AppendCommentRequest")
    })
    public void appendComment(@Valid @NotNull @PathVariable("id") String id,
                             @Valid @NotEmpty @RequestBody AppendCommentRequest appendCommentRequest){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        commentService.appendComment(userDetail.getId(), id, appendCommentRequest);

    }
}