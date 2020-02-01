package com.sm.dao.dao;

import org.apache.commons.lang3.StringUtils;
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
        try{
            jdbcTemplate.update(sql, new Object[]{term, userid});
        }catch (Exception e){

        }

    }

    public List<String> getHotSearchTop10() {
        final String sql = String.format("select search_term from %s order by cnt desc limit 10", VarProperties.HOT_SEARCH);
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> getMySearchTop10(Integer userid) {
        final String sql = String.format("select search_term from %s where user_id = ?", VarProperties.MY_SEARCH);
        return jdbcTemplate.queryForList(sql, new Object[]{userid}, String.class);
    }

    public void deleteMySearch(int id) {
        final String sql = String.format("delete from %s where user_id = ?", VarProperties.MY_SEARCH);
        jdbcTemplate.update(sql, new Object[]{id});
    }

    public void addHotSearch(String term) {
        if (!StringUtils.isNoneBlank(term)){
            return;
        }
        String sql = String.format("select id from %s where search_term = ?", VarProperties.HOT_SEARCH);
        String sql1 = String.format("update %s set cnt = cnt + 1 where id = ?", VarProperties.HOT_SEARCH);
        String sql2 = String.format("insert into %s (search_term,cnt) values (?,?)", VarProperties.HOT_SEARCH );
        try{
            Integer id = jdbcTemplate.queryForObject(sql, new Object[]{term}, Integer.class);
            if(id != null && id != 0){
                jdbcTemplate.update(sql1, new Object[]{id});
            }else{
                jdbcTemplate.update(sql2, new Object[]{term, 1});
            }
        }catch (Exception e){

        }
    }
}