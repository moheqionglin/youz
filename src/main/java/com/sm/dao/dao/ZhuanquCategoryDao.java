package com.sm.dao.dao;

import com.sm.dao.domain.ProductZhuanQuCategory;
import com.sm.dao.rowMapper.ProductZhuanQuCategoryRowMapper;
import com.sm.message.product.ZhuanquCategoryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 22:24
 */
@Component
public class ZhuanquCategoryDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public List<ProductZhuanQuCategory> getTejiaCategoryList() {
        String sql = String.format("select id,name ,image,delete_able, enable from %s", VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        return jdbcTemplate.query(sql, new ProductZhuanQuCategoryRowMapper());
    }

    public Integer create(ProductZhuanQuCategory pc) {
        String sql = String.format("insert into %s(name,image) values(:name,:image) ", VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource pmap = new MapSqlParameterSource();
        pmap.addValue("name", pc.getName());
        pmap.addValue("image", pc.getImage());
        namedParameterJdbcTemplate.update(sql, pmap, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(ZhuanquCategoryItem item) {
        String sql = String.format("update %s set name = ?, image = ? where id = ?", VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        jdbcTemplate.update(sql, new Object[]{item.getName(), item.getImage(), item.getId()});
    }

    public void delete(int categoryid) {
        String sql = String.format("delete from %s where id = ?", VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        jdbcTemplate.update(sql, new Object[]{categoryid});
    }

    public void changeAble(int categoryid, boolean ableType) {
        String sql = String.format("update %s set enable = ? where id = ?", VarProperties.PRODUCT_ZHUANQU_CATEGORY);
        jdbcTemplate.update(sql, new Object[]{ableType, categoryid});
    }

    public long countProductByZhuanQuIt(int categoryid) {
        final String sql = String.format("select count(1) from %s where zhuanqu_id = ? and zhuanqu_endTime < ?", VarProperties.PRODUCTS);
        return jdbcTemplate.queryForObject(sql, new Object[]{categoryid, new Date().getTime()}, Long.class);
    }
}