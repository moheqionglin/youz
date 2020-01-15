package com.sm.message;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页结果DO
 * @author Joetao
 */

@ApiModel(description= "分页结果")
public class PageResult<T> {
    @ApiModelProperty(value = "页大小")
    private int pageSize;
    @ApiModelProperty(value = "页码")
    private int pageNum;
    @ApiModelProperty(value = "总页数")
    private int total;
    @ApiModelProperty(value = "数据")
    private List<T> data;

    public PageResult(int pageSize, int pageNum, int total, List<T> data) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.total = total;
        this.data = data;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

}