package com.sm.dao.dao;

import com.sm.controller.TixianController;
import com.sm.message.admin.TixianInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-16 21:38
 */
@Component
public class TixianDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void creteTixian(int userid, BigDecimal amount) {
        final String sql = String.format("insert into %s (user_id, amount) values(?,?)", VarProperties.TIXIAN_APPROVE);
        jdbcTemplate.update(sql, new Object[]{userid, amount});
    }

    public List<TixianInfo> getTixianList(TixianController.TiXianType type, int pageSize, int pageNum) {

        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;

        String where = "";
        switch (type){
            case ALL:
                break;
            case WAIT_APPROVE:
                where = " and approve_status = 'WAIT_APPROVE'";
                break;
            case APPROVE_PASS:
                where = " and approve_status = 'APPROVE_PASS'";
                break;
            case APPROVE_REJECT:
                where = " and approve_status = 'APPROVE_REJECT'";
                break;
        }
        final String sql = String.format("select id,user_id,amount,approve_status,approve_id,approve_comment,created_time from %s where 1=1   %s order by id desc limit ?,?", VarProperties.TIXIAN_APPROVE, where);
        return jdbcTemplate.query(sql, new Object[]{startIndex, pageSize}, new TixianInfo.TixianInfoRowMapper());

    }

    public void approveTixian(Integer userid, Integer id, TixianController.TiXianType type) {
        final String sql = String.format("update %s set approve_status = ? , approve_id = ? where id =?", VarProperties.TIXIAN_APPROVE);
        jdbcTemplate.update(sql, new Object[]{type.toString(), userid, id});
    }

    public TixianInfo getTiXianDetail(Integer id) {
        final String sql = String.format("select id,user_id, amount, approve_status,approve_id,approve_comment from %s where id = ?",VarProperties.TIXIAN_APPROVE);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{id}, new TixianInfo.TixianInfoRowMapper());
        }catch (Exception e){
            return null;
        }

    }

    public BigDecimal getDongjieYueAmount(int userId) {
        final String sql = String.format("select sum(amount) as amount from %s where user_id = ? and approve_status = 'WAIT_APPROVE'", VarProperties.TIXIAN_APPROVE);
        try{
            BigDecimal bigDecimal = jdbcTemplate.queryForObject(sql, new Object[]{userId}, BigDecimal.class);
            return bigDecimal == null ? BigDecimal.ZERO : bigDecimal;
        }catch (Exception e){
            return BigDecimal.ZERO;
        }
    }
}