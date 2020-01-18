package com.example.auth.demo.dao;

import com.example.auth.demo.BaseTest;
import com.sm.dao.dao.AddressDao;
import com.sm.dao.dao.VarProperties;
import com.sm.message.product.SimpleProductInfo;
import net.bytebuddy.asm.Advice;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 23:37
 */
public class AddressDaoTeset extends BaseTest {
    @Autowired
    private AddressDao addressDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Test
    public void tes(){
        String psql = String.format("select id, name,profile_img from %s where id in ( :ids )", VarProperties.PRODUCTS);
        List<Integer> ids = new ArrayList<>();
        ids.add(562);
        ids.add(1463);
        ids.add(1997);
        ids.add(822);
        HashMap<String, Object> pa = new HashMap<>();
        pa.put("ids", ids);
        List<SimpleProductInfo> query1 = namedParameterJdbcTemplate.query(psql, pa, new SimpleProductInfo.SimpleProudctInfoRowMapper());
        System.out.println(query1);
        String psql1 = String.format("select id, name,profile_img from %s where id in ( ? )", VarProperties.PRODUCTS);
        List<SimpleProductInfo> query = jdbcTemplate.query(psql1, new Object[]{ids}, new SimpleProductInfo.SimpleProudctInfoRowMapper());
        System.out.println(query);
    }


}