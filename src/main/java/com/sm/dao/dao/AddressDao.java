package com.sm.dao.dao;

import com.sm.dao.domain.ShippingAddress;
import com.sm.dao.rowMapper.ShippingAddressRowMapper;
import com.sm.message.address.AddressDetailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-08 22:29
 * id ,user_id,province,city,area,shipping_address,
 * shipping_address_details ,link_person ,phone , default_address , created_time, modified_time
 */
@Component
public class AddressDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ShippingAddress> getAddressPaged(int userId) {
        String sql = "select id, link_person , phone, default_address, shipping_address, shipping_address_details, address_id " +
                "from shipping_address where user_id = ? order by default_address desc";
        List<ShippingAddress> rst = jdbcTemplate.query(sql, new Object[]{userId}, new ShippingAddressRowMapper());
        return rst;
    }

    public Integer create(int userId, AddressDetailInfo addressDetailInfo) {
        String sql = "insert into shipping_address(user_id,province,city,area,shipping_address, shipping_address_details ,link_person ,phone , default_address,address_id ) values(" +
                " :user_id, :province,:city, :area, :shipping_address, :shipping_address_details , :link_person , :phone , :default_address, :address_id)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);
        sqlParameterSource.addValue("province", addressDetailInfo.getProvince());
        sqlParameterSource.addValue("city", addressDetailInfo.getCity());
        sqlParameterSource.addValue("area", addressDetailInfo.getArea());
        sqlParameterSource.addValue("shipping_address", addressDetailInfo.getShippingAddress());
        sqlParameterSource.addValue("shipping_address_details", addressDetailInfo.getShippingAddressDetails());
        sqlParameterSource.addValue("link_person", addressDetailInfo.getLinkPerson());
        sqlParameterSource.addValue("phone", addressDetailInfo.getPhone());
        sqlParameterSource.addValue("default_address", addressDetailInfo.isDefaultAddress());
        sqlParameterSource.addValue("address_id", addressDetailInfo.getReceiveAddressManagerInfo().getId());
        namedParameterJdbcTemplate.update(sql, sqlParameterSource, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void removeDefaultAddress(int userId) {
        String sql = "update shipping_address set default_address = 0 where user_id = ?";
        jdbcTemplate.update(sql, new Object[]{userId});
    }

    @Transactional
    public void update(AddressDetailInfo addressDetailInfo) {
        String sql = "update shipping_address set " +
                "  province = :province ," +
                "  city = :city , " +
                "  area = :area ," +
                "  shipping_address = :shipping_address , " +
                "  shipping_address_details = :shipping_address_details," +
                "  link_person = :link_person," +
                "  phone = :phone, " +
                "  default_address = :default_address ,address_id =:address_id where id = :id ";
        Map paramsMap = new HashMap();
        paramsMap.put("province", addressDetailInfo.getProvince());
        paramsMap.put("city", addressDetailInfo.getCity());
        paramsMap.put("area", addressDetailInfo.getArea());
        paramsMap.put("shipping_address", addressDetailInfo.getShippingAddress());
        paramsMap.put("shipping_address_details", addressDetailInfo.getShippingAddressDetails());
        paramsMap.put("link_person", addressDetailInfo.getLinkPerson());
        paramsMap.put("phone", addressDetailInfo.getPhone());
        paramsMap.put("default_address", addressDetailInfo.isDefaultAddress());
        paramsMap.put("id", addressDetailInfo.getId());
        paramsMap.put("address_id", addressDetailInfo.getReceiveAddressManagerInfo().getId());
        if(addressDetailInfo.isDefaultAddress()){
            jdbcTemplate.update("update shipping_address set default_address = false where user_id = ?", new Object[]{addressDetailInfo.getUserId()});
        }
        namedParameterJdbcTemplate.update(sql, paramsMap);


    }
    public void delete(int userid, int addressId) {
        String sql = "delete from shipping_address where  id = ? and user_id = ?";
        jdbcTemplate.update(sql, new Object[]{addressId, userid});
    }

    public ShippingAddress getAddressDetail(int userId, int addressId) {
        String sql = "select id ,user_id,province,city,area,shipping_address, shipping_address_details ,link_person ,phone , default_address, address_id from " +
                "shipping_address where id = ?";
        try {

            ShippingAddress shippingAddress = jdbcTemplate.queryForObject(sql, new Object[]{addressId}, new ShippingAddressRowMapper());
            if(shippingAddress.getUserId() != userId){
                return null;
            }
            return shippingAddress;
        }catch (Exception e){
            return null;
        }

    }

    public ShippingAddress getDefaultAddress(int userId) {
        String sql = "select id, link_person , phone, default_address, shipping_address, shipping_address_details , address_id " +
                "from shipping_address where user_id = ? order by default_address limit 1";
        return jdbcTemplate.query(sql, new Object[]{userId}, new ShippingAddressRowMapper()).stream().findFirst().orElse(null);

    }

    public void removeAddressId(int id) {
        final String sql = String.format("update %s  set address_id = 0 where address_id = ?", VarProperties.SHIPPING_ADDRESS);
        jdbcTemplate.update(sql, new Object[]{id});
    }
}