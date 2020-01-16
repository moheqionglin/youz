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
    @NotNull
    private String searchTerm;
    @NotNull
    private boolean show;

    private Integer firstCatalog;
    private Integer secondCatalog;

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

    public Integer getFirstCatalog() {
        return firstCatalog;
    }

    public void setFirstCatalog(Integer firstCatalog) {
        this.firstCatalog = firstCatalog;
    }

    public Integer getSecondCatalog() {
        return secondCatalog;
    }

    public void setSecondCatalog(Integer secondCatalog) {
        this.secondCatalog = secondCatalog;
    }
}