package com.sm.dao.dao;

import com.sm.dao.domain.TouTiao;
import com.sm.dao.rowMapper.TouTiaoRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 17:23
 */
@Component
public class ToutiaoDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public TouTiao getLatestToutiao() {
        final String sql = String.format("select id ,title , content,created_time from %s order by id desc limit 1", VarProperties.TOUTIAO);
        return jdbcTemplate.query(sql, new TouTiaoRowMapper()).stream().findFirst().orElse(null);
    }

    public Integer create(TouTiao touTiao) {
        final String sql = String.format("insert into %s (title, content) values(:title, :content)", VarProperties.TOUTIAO);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource ps = new MapSqlParameterSource();
        ps.addValue("title", touTiao.getTitle());
        ps.addValue("content", touTiao.getContent());
        namedParameterJdbcTemplate.update(sql, ps, keyHolder);
        return keyHolder.getKey().intValue();

    }

    public void update(TouTiao touTiao) {
        final String sql = String.format("update %s set title =?, content =?", VarProperties.TOUTIAO);
        jdbcTemplate.update(sql, new Object[]{touTiao.getTitle(), touTiao.getContent()});
    }

    public void delete(Integer id) {
        final String sql = String.format("delete from %s where id = ?", VarProperties.TOUTIAO);
        jdbcTemplate.update(sql, new Object[]{id});
    }

    public List<TouTiao> getToutiaoList(int pageSize, int pageNum) {
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select id ,title , content,created_time from %s order by id desc limit ?, ?", VarProperties.TOUTIAO);
        return jdbcTemplate.query(sql, new Object[]{startIndex, pageSize}, new TouTiaoRowMapper());
    }
}