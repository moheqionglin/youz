package com.sm.service;

import com.sm.dao.dao.SearchDao;
import com.sm.message.search.SearchHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 20:20
 */
@Component
public class SearchService {
    @Autowired
    private SearchDao searchDao;

    public void addMySearchTerm(Integer userid, String term) {
        searchDao.addMySearchTerm(userid, term);
    }

    public SearchHistory getHotSearchAndMySearchHistory(Integer userid) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setHotSearch(searchDao.getHotSearchTop10());
        if(userid != null){
            searchHistory.setMySearch(searchDao.getMySearchTop10(userid));
        }
        return searchHistory;
    }

    public void deleteMySearch(int id) {
        searchDao.deleteMySearch(id);
    }

    public void addHotSearch(String term) {
        searchDao.addHotSearch(term);
    }
}