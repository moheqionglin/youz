package com.sm.dao.dao;

import com.sm.dao.domain.ProductSupplier;
import com.sm.dao.rowMapper.ProductSupplierRowMapper;
import com.sm.message.product.SupplierInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-11 19:57
 * id, name, contact_person, phone, created_time, modified_time
 */
@Component
public class SupplierDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ProductSupplier> getAll() {
        final String sql = String.format("select id, name, contact_person, phone from %s", VarProperties.PRODUCT_SUPPLIERS);
        return jdbcTemplate.query(sql, new ProductSupplierRowMapper());
    }

    public Integer create(ProductSupplier generateDomain) {
        final String sql = String.format("insert into %s(name, contact_person, phone) values(:name, :contact_person, :phone)", VarProperties.PRODUCT_SUPPLIERS);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource pmap = new MapSqlParameterSource();
        pmap.addValue("name", generateDomain.getName());
        pmap.addValue("contact_person", generateDomain.getContactPerson());
        pmap.addValue("phone", generateDomain.getPhone());
        namedParameterJdbcTemplate.update(sql, pmap, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void update(SupplierInfo supplierInfo) {
        final String sql =  String.format("update %s set name = ?, contact_person = ? , phone = ? where id = ?", VarProperties.PRODUCT_SUPPLIERS);
        jdbcTemplate.update(sql, new Object[]{supplierInfo.getName(), supplierInfo.getContactPerson(), supplierInfo.getPhone(), supplierInfo.getId()});
    }

    public void delete(int supplierId) {
        final String sql =  String.format("delete from %s  where id = ?", VarProperties.PRODUCT_SUPPLIERS);
        jdbcTemplate.update(sql, new Object[]{supplierId});

    }

    public ProductSupplier get(int supplierId) {
        final String sql = String.format("select id, name, contact_person, phone from %s", VarProperties.PRODUCT_SUPPLIERS);
        return jdbcTemplate.query(sql, new ProductSupplierRowMapper()).stream().findFirst().orElse(null);
    }
}