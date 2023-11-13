package com.sm.dao.dao;

import com.sm.dao.domain.ReceiveAddressManager;
import com.sm.dao.domain.ShippingAddress;
import com.sm.dao.rowMapper.ShippingAddressRowMapper;
import com.sm.message.admin.ReceiveAddressManagerInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReceiveAddressManagerDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void add(ReceiveAddressManager receiveAddressManager) {
        final String sql = String.format("insert into %s(address_name, address_detail,delivery_fee, tuangou_enable, tuangou_threshold ) values(?,?,?,?,?)", VarProperties.RECEIVE_ADDRESS_MANAGER);
        jdbcTemplate.update(sql,  new Object[]{receiveAddressManager.getAddressName(), receiveAddressManager.getAddressDetail(),receiveAddressManager.getDeliveryFee(), receiveAddressManager.isTuangouEnable(),receiveAddressManager.getTuangouThreshold()});
    }

    public void update(ReceiveAddressManager receiveAddressManager) {
        final String sql = String.format("update %s set address_name = ? , address_detail = ? ,tuangou_enable =?, tuangou_threshold =? , delivery_fee =? where id = ?", VarProperties.RECEIVE_ADDRESS_MANAGER);
        jdbcTemplate.update(sql, new Object[]{receiveAddressManager.getAddressName(), receiveAddressManager.getAddressDetail(),
                receiveAddressManager.isTuangouEnable(), receiveAddressManager.getTuangouThreshold(), receiveAddressManager.getDeliveryFee(),
                receiveAddressManager.getId()});
    }

    public void delete(int id) {
        final String sql = String.format("update %s  set is_del = 1 where id = ?", VarProperties.RECEIVE_ADDRESS_MANAGER);
        jdbcTemplate.update(sql, new Object[]{id});
    }

    public List<ReceiveAddressManager> queryAddressList() {
        String sql = String.format("select id, address_name , address_detail, delivery_fee, tuangou_enable, tuangou_threshold " +
                "from %s where is_del = 0", VarProperties.RECEIVE_ADDRESS_MANAGER);
        return jdbcTemplate.query(sql, new ReceiveAddressManagerInfo.ReceiveAddressManagerMapper());
    }

    public ReceiveAddressManager queryAddressDetail(int id) {
        String sql = String.format("select id, address_name , address_detail , delivery_fee, tuangou_enable, tuangou_threshold " +
                "from %s where is_del = 0 and id = ?", VarProperties.RECEIVE_ADDRESS_MANAGER);
        return jdbcTemplate.query(sql, new Object[]{id}, new ReceiveAddressManagerInfo.ReceiveAddressManagerMapper()).stream().findFirst().orElse(null);
    }

    public List<ReceiveAddressManager> queryAddressDetail(List<Integer> ids) {
        String sql = String.format("select id, address_name , address_detail , delivery_fee, tuangou_enable, tuangou_threshold " +
                "from %s where is_del = 0 and id in(:ids)", VarProperties.RECEIVE_ADDRESS_MANAGER);
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids",ids), new ReceiveAddressManagerInfo.ReceiveAddressManagerMapper());
    }
}
