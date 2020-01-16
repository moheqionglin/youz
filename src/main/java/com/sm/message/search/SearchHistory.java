package com.sm.message.search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 20:30
 */
public class SearchHistory {
    List<String> mySearch = new ArrayList<>();
    List<String> hotSearch = new ArrayList<>();

    public List<String> getMySearch() {
        return mySearch;
    }

    public void setMySearch(List<String> mySearch) {
        this.mySearch = mySearch;
    }

    public List<String> getHotSearch() {
        return hotSearch;
    }

    public void setHotSearch(List<String> hotSearch) {
        this.hotSearch = hotSearch;
    }
}