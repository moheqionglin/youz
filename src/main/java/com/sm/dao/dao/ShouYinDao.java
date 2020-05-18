package com.sm.dao.dao;

import com.sm.message.shouyin.ShouYinCartInfo;
import com.sm.message.shouyin.ShouYinCartItemInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ShouYinDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ShouYinCartInfo getAllCartItems(Integer userId) {
        List<ShouYinCartItemInfo> rst = jdbcTemplate.query("select user_id, product_id, product_profile_img, product_name, product_size, product_cnt, unit_price from shouyin_cart where user_id = ?",
                new Object[]{userId}, new ShouYinCartItemInfo.ShouYinCartItemInfoRowMapper());
        BigDecimal total = rst.stream().map(i -> i.getUnitPrice()
                .multiply(BigDecimal.valueOf(i.getProductCnt())))
                .reduce(BigDecimal::add).get();

        return new ShouYinCartInfo(total, rst);
    }
}
