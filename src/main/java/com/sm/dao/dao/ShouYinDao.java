package com.sm.dao.dao;

import com.sm.controller.ShoppingCartController;
import com.sm.controller.ShouYinController;
import com.sm.message.order.ShouYinFinishOrderInfo;
import com.sm.message.product.ShouYinProductInfo;
import com.sm.message.shouyin.*;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ShouYinDao {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ShouYinCartInfo getAllCartItems(Integer userId) {
        List<ShouYinCartItemInfo> rst = jdbcTemplate.query("select id, user_id, product_id, product_profile_img, product_name, product_size, product_cnt, unit_price, cost_price from shouyin_cart where user_id = ? order by id desc",
                new Object[]{userId}, new ShouYinCartItemInfo.ShouYinCartItemInfoRowMapper());
        BigDecimal total = rst.stream().map(i -> i.getUnitPrice()
                .multiply(BigDecimal.valueOf(i.getProductCnt())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        PersonWorkStatusInfo latestPersonWorkStatus = getLatestPersonWorkStatus(userId);
        ShouYinCartInfo shouYinCartInfo = new ShouYinCartInfo(total, rst);
        if(latestPersonWorkStatus != null && latestPersonWorkStatus.getStatus().equalsIgnoreCase(ShouYinController.SHOUYIN_PERSON_STATUS.WORKING.toString())){
            shouYinCartInfo.setStartTime(SmUtil.parseLongToTMDHMS(latestPersonWorkStatus.getStartTime() * 1000));
        }
        return shouYinCartInfo;
    }

    public void creteOrUpdateCartItem(int userId, ShouYinProductInfo shouYinProductByCode) {
        final String existsSql = String.format("select id from %s where product_id = ? and user_id = ?", VarProperties.SHOU_YIN_CART);
        final String updateSql = String.format("update %s set product_cnt = product_cnt + 1 where id = ?",  VarProperties.SHOU_YIN_CART);
        final String insertSql = String.format("insert into %s (user_id,product_id, product_profile_img,product_name,product_size,product_cnt, unit_price,cost_price) values (?,?,?,?,?,?,?,?) ", VarProperties.SHOU_YIN_CART);

        if(shouYinProductByCode.isSanZhuang()){
            jdbcTemplate.update(insertSql, new Object[]{userId, shouYinProductByCode.getProductId(), shouYinProductByCode.getProductProfileImg(), shouYinProductByCode.getProductName(), shouYinProductByCode.getProductSize(), 1, shouYinProductByCode.getUnitPrice(), shouYinProductByCode.getCostPrice()});
            return;
        }

        Integer cartId = null;
        try{
            cartId = jdbcTemplate.queryForObject(existsSql, new Object[]{shouYinProductByCode.getProductId(), userId}, Integer.class);
        }catch (Exception e){

        }

        if(cartId == null || cartId <= 0){
            jdbcTemplate.update(insertSql, new Object[]{userId, shouYinProductByCode.getProductId(), shouYinProductByCode.getProductProfileImg(), shouYinProductByCode.getProductName(), shouYinProductByCode.getProductSize(), 1, shouYinProductByCode.getUnitPrice(), shouYinProductByCode.getCostPrice()});
        }else{
            jdbcTemplate.update(updateSql, cartId);
        }
    }

    public void addCartWithNoCode(int userId, BigDecimal price) {
        final String insertSql = String.format("insert into %s (user_id,product_id, product_profile_img,product_name,product_size,product_cnt, unit_price,cost_price) " +
                "values (?,?,?,?,?,?,?,?) ", VarProperties.SHOU_YIN_CART);
        jdbcTemplate.update(insertSql, new Object[]{userId, 0, null, "综合商品", "特殊尺寸", 1, price, price});
    }

    public void deleteCartItem(List<Integer> cartItemIds) {
        final String sql = String.format("delete from %s where id in ( :ids )", VarProperties.SHOU_YIN_CART);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("ids", cartItemIds);
        namedParameterJdbcTemplate.update(sql, mapSqlParameterSource);
    }

    @Transactional
    public void createOrder(int userId, String orderNum, ShouYinCartInfo allCartItems) {
        BigDecimal totalPrice = allCartItems.getTotal();
        final List<ShouYinCartItemInfo> items = allCartItems.getItems();

        BigDecimal totalCostPrice = items.stream().map(item -> item.getCostPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        final String orderSql = String.format("insert into %s (order_num, user_id, total_cost_price,  total_price,had_pay_money,offline_pay_money, online_pay_money) values" +
                "(:order_num, :user_id, :total_cost_price,  :total_price,:had_pay_money,:offline_pay_money, :online_pay_money)", VarProperties.SHOUYIN_ORDER);
        final String orderItemSql = String.format("insert into %s (order_id,product_id,product_profile_img,product_name,product_size,product_cnt,unit_price,cost_price) values (?,?,?,?,?,?,?,?)", VarProperties.SHOUYIN_ORDER_ITEM);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("order_num", orderNum);
        mapSqlParameterSource.addValue("user_id", userId);
        mapSqlParameterSource.addValue("total_cost_price", totalCostPrice);
        mapSqlParameterSource.addValue("total_price", totalPrice);
        mapSqlParameterSource.addValue("had_pay_money", BigDecimal.ZERO);
        mapSqlParameterSource.addValue("offline_pay_money", BigDecimal.ZERO);
        mapSqlParameterSource.addValue("online_pay_money", BigDecimal.ZERO);
        namedParameterJdbcTemplate.update(orderSql, mapSqlParameterSource, keyHolder);
        int orderaId = keyHolder.getKey().intValue();
        jdbcTemplate.batchUpdate(orderItemSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ShouYinCartItemInfo si = items.get(i);
                ps.setInt(1, orderaId);
                ps.setInt(2, si.getProductId());
                ps.setString(3, si.getProductProfileImg());
                ps.setString(4, si.getProductName());
                ps.setString(5, si.getProductSize());
                ps.setInt(6, si.getProductCnt());
                ps.setBigDecimal(7, si.getUnitPrice());
                ps.setBigDecimal(8, si.getCostPrice());
            }
            @Override
            public int getBatchSize() {
                return items.size();
            }
        });

        //删除
        final String sql = String.format("delete from %s where user_id = ?", VarProperties.SHOU_YIN_CART);
        jdbcTemplate.update(sql, new Object[]{userId});
    }

    public BigDecimal cancelOrder(String orderNum) {
        BigDecimal drawback = BigDecimal.ZERO;
        final String sql = String.format("select had_pay_money from %s where order_num = ?", VarProperties.SHOUYIN_ORDER);
        final String calcelSql = String.format("update %s set  status = ? where order_num = ?", VarProperties.SHOUYIN_ORDER);
        try{
            drawback = jdbcTemplate.queryForObject(sql, new Object[]{orderNum}, BigDecimal.class);
        }catch (Exception e){

        }
        jdbcTemplate.update(calcelSql, new Object[]{ShouYinController.SHOUYIN_ORDER_STATUS.CANCEL.toString(), orderNum});
        return drawback;
    }

    public boolean updateCount(int userId, int cartItemId, ShoppingCartController.CountAction action) {
        final String addSql = String.format("update %s set product_cnt = product_cnt + 1 where id = ? and user_id = ?", VarProperties.SHOU_YIN_CART);
        final String reductSql = String.format("update %s set product_cnt = product_cnt - 1 where id = ? and user_id = ? and product_cnt > 1", VarProperties.SHOU_YIN_CART);
        String sql = null;
        switch (action){
            case ADD:
                sql = addSql;
                break;
            case REDUCE:
                sql = reductSql;
                break;
        }
        if(StringUtils.isEmpty(sql)){
            return false;
        }
        jdbcTemplate.update(sql, new Object[]{cartItemId, userId});
        return true;
    }

    public ShouYinFinishOrderInfo getUnfinishOrder(int userId) {
        final String sql = "select id, order_num, user_id, total_cost_price,total_price,had_pay_money,offline_pay_money, online_pay_money,status from shouyin_order where status = 'WAIT_PAY' and user_id = ? ";

        ShouYinFinishOrderInfo syfi = jdbcTemplate.query(sql, new Object[]{userId}, new ShouYinFinishOrderInfo.ShouYinFinishOrderInfoRowMapper()).stream().findFirst().orElse(null);
        if(syfi != null && syfi.getNeedPay().compareTo(BigDecimal.ZERO) <= 0){
            setFinishStatus(syfi.getOrderNum());
            return null;
        }
        return syfi;
    }


    public void setFinishStatus(String orderNum) {
        final String updateSql = "update shouyin_order set status = '"+ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString()+"' where order_num = ?";
        jdbcTemplate.update(updateSql, new Object[]{orderNum});
    }

    public ShouYinFinishOrderInfo getShouYinFinishOrderInfo(String orderNum) {
        final String sql = "select id,order_num, user_id, total_cost_price,total_price,had_pay_money,offline_pay_money, online_pay_money,status from shouyin_order where order_num = ? ";
        return jdbcTemplate.query(sql, new Object[]{orderNum}, new ShouYinFinishOrderInfo.ShouYinFinishOrderInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public void payWithCash(String orderNum, BigDecimal hadPayMoney, BigDecimal offlinePayMoney, ShouYinController.SHOUYIN_ORDER_STATUS status) {
        final String sql = String.format("update %s set had_pay_money = ?, offline_pay_money = ?, status = ? where order_num = ?", VarProperties.SHOUYIN_ORDER);
        jdbcTemplate.update(sql, new Object[]{hadPayMoney, offlinePayMoney, status.toString(), orderNum});
    }

    public void payOnLine(String orderNum, BigDecimal total, BigDecimal needPay) {
        final String sql = String.format("update %s set had_pay_money = ?, online_pay_money = ?, status = ? where order_num = ?", VarProperties.SHOUYIN_ORDER);
        jdbcTemplate.update(sql, new Object[]{total, needPay, ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString(), orderNum});
    }

    public PersonWorkStatusInfo getLatestPersonWorkStatus(int userId) {
        final String sql = String.format("select id, user_id, backup_amount, start_time,end_time,status from %s where user_id = ? order by id desc", VarProperties.SHOUYIN_WORK_RECORD);
        return jdbcTemplate.query(sql, new Object[]{userId}, new PersonWorkStatusInfo.PersonWorkStatusInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public void kaigong(int userID, BigDecimal backAmount) {
        final String kaigong = String.format("insert into %s( user_id, backup_amount, start_time) values(?,?,?)", VarProperties.SHOUYIN_WORK_RECORD);
        jdbcTemplate.update(kaigong, new Object[]{userID, backAmount, System.currentTimeMillis()/1000, });
    }

    public void shouGong(int userId, int recordId, long endTime) {
        final String sql = String.format("update %s set end_time = ?, status = ? where id = ?", VarProperties.SHOUYIN_WORK_RECORD);
        jdbcTemplate.update(sql, new Object[]{endTime, ShouYinController.SHOUYIN_PERSON_STATUS.FINISH.toString(), recordId});
    }

    public ShouYinWorkRecordStatisticsInfo getShouYinWorkRecordStatisticsInfo(int userId, long startTime, long endTime) {

        final String sql = String.format("select count(1) as orderCnt, sum(total_price) as totalOrderAmount, sum(offline_pay_money) as totalOfflineAmount, sum(online_pay_money) as totalOnlineAmount from %s where user_id = ? and created_time between ? and ? and status = '"+ShouYinController.SHOUYIN_ORDER_STATUS.FINISH.toString()+"'", VarProperties.SHOUYIN_ORDER);
        return jdbcTemplate.queryForObject(sql, new Object[]{userId, new Date(startTime*1000), new Date(endTime*1000)}, new ShouYinWorkRecordStatisticsInfo.ShouYinWorkRecordStatisticsInfoRowMapper());
    }

    public ShouYinOrderInfo queryOrder(String orderNum) {
        String orderSql = "select id, order_num, user_id, total_cost_price,total_price,had_pay_money,offline_pay_money, online_pay_money,status from shouyin_order where order_num = ? ";
        String orderItemsql = "select order_id,product_id,product_profile_img,product_name,product_size,product_cnt,unit_price from shouyin_order_item where order_id = ?";
        ShouYinOrderInfo orderInfo = null;
        try{
            orderInfo = jdbcTemplate.queryForObject(orderSql, new Object[]{orderNum}, new ShouYinOrderInfo.ShouYinOrderInfoRowMapper());
        }catch (Exception e){
            logger.error("Get order empty order num = {}", orderNum);
        }
        if(orderInfo == null){
            return null;
        }
        List<ShouYinOrderItemInfo> items = jdbcTemplate.query(orderItemsql, new Object[]{orderInfo.getId()}, new ShouYinOrderItemInfo.ShouYinOrderItemInfoRowMapper());
        orderInfo.getShouYinOrderItemInfoList().addAll(items);
        return orderInfo;
    }

    public void reduceStock(Integer id) {

        final String sql = String.format("select product_id, product_cnt from %s where order_id = ? and product_id > 0", VarProperties.SHOUYIN_ORDER_ITEM);
        List<Map<String, Object>> rsts = jdbcTemplate.queryForList(sql, new Object[]{id});
        HashMap<Integer, Integer> pid2Stock = new HashMap<>();
        rsts.stream().forEach(kv -> {
            pid2Stock.put(Integer.valueOf(kv.get("product_id").toString()), Integer.valueOf(kv.get("product_cnt").toString()));
        });

        String stockSql = String.format("select id,stock from %s where id in (:ids)", VarProperties.PRODUCTS);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("ids", pid2Stock.keySet());

        List<Map<String, Object>> oldStocks = namedParameterJdbcTemplate.queryForList(stockSql, mapSqlParameterSource);
        List<Object[]> collect = oldStocks.stream().map(m -> {
            int pid = Integer.valueOf(m.get("id").toString());
            int stock = Integer.valueOf(m.get("stock").toString());
            int newStock = stock - (pid2Stock.get(pid) == null ? 0 : pid2Stock.get(pid));
            return new Object[]{newStock < 0 ? 0 : newStock, pid};
        }).collect(Collectors.toList());
        final String reductStockSql = String.format("update %s set stock =  ? where id = ?", VarProperties.PRODUCTS);

        jdbcTemplate.batchUpdate(reductStockSql, collect);


    }
}
