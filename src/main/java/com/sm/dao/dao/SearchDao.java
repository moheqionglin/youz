package com.sm.dao.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 20:20
 */
@Component
public class SearchDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addMySearchTerm(Integer userid, String term) {
        final String sql = String.format("insert into %s(search_term, user_id)values (?,?)", VarProperties.MY_SEARCH);
        jdbcTemplate.update(sql, new Object[]{term, userid});
    }

    public List<String> getHotSearchTop10() {
        final String sql = String.format("select search_term from %s order by cnt desc limit 10", VarProperties.HOT_SEARCH);
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getMySearchTop10(Integer userid) {
        final String sql = String.format("select search_term from %s where user_id = ?", VarProperties.MY_SEARCH);
        return jdbcTemplate.queryForList(sql, new Object[]{userid}, String.class);
    }
}