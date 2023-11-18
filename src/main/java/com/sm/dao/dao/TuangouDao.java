package com.sm.dao.dao;

import com.sm.controller.TuangouController;
import com.sm.dao.domain.ReceiveAddressManager;
import com.sm.dao.domain.Tuangou;
import com.sm.dao.rowMapper.TuangouRowMapper;
import com.sm.message.tuangou.TuangouListItemInfo;
import com.sm.message.tuangou.TuangouSelfStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class TuangouDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public static enum TUANGOU_STATUS {开团,成团,取消;

        public static TUANGOU_STATUS getByQueryType(TuangouController.StatusType queryType) {
            TuangouDao.TUANGOU_STATUS status = null;
            if(TuangouController.StatusType.ING.equals(queryType)){
                status = TuangouDao.TUANGOU_STATUS.开团;
            }else if(TuangouController.StatusType.SUCESS.equals(queryType)){
                status = TuangouDao.TUANGOU_STATUS.成团;
            }else if(TuangouController.StatusType.CALCEL.equals(queryType)){
                status = TuangouDao.TUANGOU_STATUS.取消;
            }
            return status;
        }
    }

    @Autowired
    private ReceiveAddressManagerDao receiveAddressManagerDao;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int finishTuangou(Tuangou tuangou) {
        final String sql = String.format("update %s set status = ? , order_count = order_count + 1 where id = ? AND order_count = ?", VarProperties.TUANGOU);
        return jdbcTemplate.update(sql, new Object[]{TUANGOU_STATUS.成团.name(), tuangou.getId(), tuangou.getOrderCount()});
    }

    public void calcelTuangou(int id) {
        final String sql = String.format("update %s set status = ? where id = ? ", VarProperties.TUANGOU);
        jdbcTemplate.update(sql, new Object[]{TUANGOU_STATUS.取消.name(), id});
    }

    public int incrTuangou(Tuangou tuangou){
        final String sql = String.format("update %s set order_count = order_count + 1  where order_count = ? and id = ?", VarProperties.TUANGOU);
        return jdbcTemplate.update(sql, new Object[]{tuangou.getOrderCount(), tuangou.getId()});
    }

    public Tuangou selectById(Integer tuangouId) {
        final String sql = String.format("select id,threshold, order_count, receive_address_manager_id, status, version from %s where id = ?", VarProperties.TUANGOU);
        return jdbcTemplate.query(sql, new Object[]{tuangouId}, new TuangouRowMapper()).stream().findFirst().orElse(null);
    }

    public int countIngByXiaoquId(int addressId) {
        final String sql = String.format("select count(1) from %s where receive_address_manager_id = ? and status = '开团'", VarProperties.TUANGOU);
        return jdbcTemplate.queryForObject(sql, new Object[]{addressId}, Integer.class);
    }

    public Tuangou selectIngByAdminAddressId(int addressId) {
        final String sql = String.format("select id,threshold, order_count, receive_address_manager_id, status, version from %s where receive_address_manager_id = ? and status = ? order by id desc limit 1", VarProperties.TUANGOU);
        return jdbcTemplate.query(sql, new Object[]{addressId, TUANGOU_STATUS.开团.name() }, new TuangouRowMapper()).stream().findFirst().orElse(null);
    }

    public Integer createTuangou(int addressId) {
        ReceiveAddressManager receiveAddressManager = receiveAddressManagerDao.queryAddressDetail(addressId);
        final String sql = String.format("insert into %s (threshold, order_count, receive_address_manager_id, status, version) values (:threshold,:order_count,:receive_address_manager_id,:status,:version)", VarProperties.TUANGOU);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource pams = new MapSqlParameterSource();
        pams.addValue("threshold", receiveAddressManager.getTuangouThreshold());
        pams.addValue("order_count", 0);
        pams.addValue("receive_address_manager_id", addressId);
        pams.addValue("status", TUANGOU_STATUS.开团.name());
        pams.addValue("version", 0);
        namedParameterJdbcTemplate.update(sql, pams, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public List<Integer> getTuangouIdsPagedByStatusAndAddressId(TuangouDao.TUANGOU_STATUS status, int pageSize, int pageNum, int addressId) {
        if(pageNum <= 0){
            pageNum = 1;
        }
        final String sql = String.format("select id from %s where status = ? and receive_address_manager_id =? order by id desc limit ?,?", VarProperties.TUANGOU);
        return jdbcTemplate.queryForList(sql, new Object[]{status.name(), addressId, (pageNum - 1) * pageSize, pageSize}, Integer.class);
    }
    public List<Tuangou> loadAllIngTuangou() {
        final String sql = String.format("select id,threshold, order_count, receive_address_manager_id, status, version from %s where status = ? ", VarProperties.TUANGOU);
        return jdbcTemplate.query(sql, new Object[]{TUANGOU_STATUS.开团.name() }, new TuangouRowMapper());
    }


    public List<Tuangou> getTuangouDetailsByIds(List<Integer> tuangouIDs) {
        final String sql = String.format("select id,threshold, order_count, receive_address_manager_id, status, version, created_time,modified_time from %s where id in (:ids) ", VarProperties.TUANGOU);
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", tuangouIDs), new TuangouRowMapper());
    }

    public List<Integer> filterByStatusAndId(List<Integer> queryTuangouId, TUANGOU_STATUS status) {
        if(CollectionUtils.isEmpty(queryTuangouId)){
            return new ArrayList<>();
        }
        final String sql = String.format("select id from %s where status = :status and id in (:ids)", VarProperties.TUANGOU);
        HashMap<String, Object> pams = new HashMap<>();
        pams.put("status", status.name());
        pams.put("ids", queryTuangouId);
        return namedParameterJdbcTemplate.queryForList(sql, pams, Integer.class);
    }
}
