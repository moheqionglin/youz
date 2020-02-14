package com.sm.message.search;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 22:53
 */
@Valid
public class SearchRequest {

    private String barcode;
    @NotNull
    private String searchTerm;
    @NotNull
    private boolean show;

    private Integer categoryId;
    @NotNull
    private SearchType type;


    public static enum SearchType {
        ALL,
        FIRST,
        SECOND,
        ALL_NOT_ZHUANQU,
        ZHUAN_QU
    }
    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public SearchType getType() {
        return type;
    }

    public void setType(SearchType type) {
        this.type = type;
    }
}