package com.sm.dao.dao;

import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import com.sm.message.order.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 21:03
 */
@Component
public class OrderDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer createOrder(CreateOrderInfo order) {
        final String sql = String.format("insert into %s (order_num, user_id, address_id, address_detail ,address_contract , yongjin_code , status " +
                "    total_cost_price,total_price ,use_yongjin ,use_yue , need_pay_money , had_pay_money ,message) values(" +
                ":order_num, :user_id, :address_id, :address_detail ,:address_contract , :yongjin_code , :status " +
                "    :total_cost_price,:total_price ,:use_yongjin ,:use_yue , :need_pay_money , :had_pay_money ,:message)", VarProperties.ORDER);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("order_num", order.getOrderNum());
        mapSqlParameterSource.addValue("user_id", order.getUserId());
        mapSqlParameterSource.addValue("address_id", order.getAddressId());
        mapSqlParameterSource.addValue("address_detail", order.getAddressDetail());
        mapSqlParameterSource.addValue("address_contract", order.getAddressContract());
        mapSqlParameterSource.addValue("yongjin_code", order.getYongjinCode());
        mapSqlParameterSource.addValue("status", order.getStatus());
        mapSqlParameterSource.addValue("total_cost_price", order.getTotalCostPrice());
        mapSqlParameterSource.addValue("total_price", order.getTotalPrice());
        mapSqlParameterSource.addValue("use_yongjin", order.getUseYongjin());
        mapSqlParameterSource.addValue("use_yue", order.getUseYue());
        mapSqlParameterSource.addValue("need_pay_money", order.getNeedPayMoney());
        mapSqlParameterSource.addValue("had_pay_money", order.getHadPayMoney());
        mapSqlParameterSource.addValue("message", order.getMessage());
        namedParameterJdbcTemplate.update(sql, mapSqlParameterSource, keyHolder);
        return keyHolder.getKey().intValue();
    }


    public void createOrderItems(Integer id, List<CreateOrderItemInfo> collect) {


        final String sql = "insert into %s(order_id ,product_id,product_name ,product_profile_img , product_size , product_cnt ,product_total_price, product_unit_price , product_sanzhuang) " +
                "values(:order_id ,:product_id,:product_name ,:product_profile_img , :product_size , :product_cnt ,:product_total_price, :product_unit_price , :product_sanzhuang)";

        MapSqlParameterSource[] pss = new MapSqlParameterSource[collect.size()];
        for(int i = 0 ; i < collect.size(); i++){
            MapSqlParameterSource pams = new MapSqlParameterSource();
            CreateOrderItemInfo ci = collect.get(i);
            pams.addValue("order_id", ci.getOrderId());
            pams.addValue("product_id", ci.getProductId());
            pams.addValue("product_name", ci.getProductName());
            pams.addValue("product_profile_img", ci.getProductProfileImg());
            pams.addValue("product_size", ci.getProductSize());
            pams.addValue("product_cnt", ci.getProductCnt());
            pams.addValue("product_total_price", ci.getProductTotalPrice());
            pams.addValue("product_unit_price", ci.getProductUnitPrice());
            pams.addValue("product_sanzhuang", ci.isProductSanzhuang());
            pss[i] = pams;
        }
        namedParameterJdbcTemplate.batchUpdate(sql, pss);
    }

    public void actionOrderStatus(String orderNum, OrderController.BuyerOrderStatus actionType) {
        final String sql = String.format("update %s set status = ? where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{actionType.toString(), orderNum});

    }

    public void actionDrawbackStatus(String orderNum, OrderController.DrawbackStatus status) {
        final String sql = String.format("update %s set drawback_status = ? where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{status.toString(), orderNum});
    }
    public Integer creteDrawbackOrder(int userId, Integer order_id, DrawbackRequest drawbackRequest, BigDecimal price, BigDecimal yue, BigDecimal yongjin) {
        final String sql = String.format("insert into %s(order_id, drawback_type, drawback_reason, drawback_detail, drawback_pay_price, drawback_yue, drawback_yongjin, drawback_imgs) " +
                "values (:order_id, :drawback_type, :drawback_reason, :drawback_detail, :drawback_pay_price, :drawback_yue, :drawback_yongjin, :drawback_imgs)", VarProperties.ORDER_DRAWBACK);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource pams = new MapSqlParameterSource();
        pams.addValue("order_id", order_id);
        pams.addValue("drawback_type", drawbackRequest.getType());
        pams.addValue("drawback_reason", drawbackRequest.getReason());
        pams.addValue("drawback_detail", drawbackRequest.getDetail());
        pams.addValue("drawback_pay_price", price);
        pams.addValue("drawback_yue", yue);
        pams.addValue("drawback_yongjin", yongjin);
        String imgs = drawbackRequest == null ? "" : drawbackRequest.getImages().stream().collect(Collectors.joining(" | "));
        pams.addValue("drawback_imgs", imgs);
        namedParameterJdbcTemplate.update(sql, pams, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public SimpleOrder getSimpleOrder(String orderNum){
        final String sql = String.format("select id,user_id,status,order_num drawback_status ,jianhuoyuan_id, jianhuo_status from %s where order_num = ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderNum}, new SimpleOrder.SimpleOrderRowMapper()).stream().findFirst().orElse(null);
    }
    public boolean existsOrder(Integer userId, String orderNum) {
        final String sql = String.format("select user_id from %s where orderNum = ? ", VarProperties.ORDER);
        Integer ouserId = null;
        try{
            ouserId = jdbcTemplate.queryForObject(sql, new Object[]{orderNum}, Integer.class);
        }catch (Exception e){
            return false;
        }
        return ouserId.equals(userId);
    }

    public DrawBackAmount getDrawbackAmount(String orderNum) {
        final String sql = String.format("select total_price, use_yongjin ,use_yue , need_pay_money, had_pay_money , chajia_status,chajia_price,chajia_use_yongjin ,chajia_use_yue , chajia_need_pay_money , chajia_had_pay_money from %s where order_num = ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderNum}, new DrawBackAmount.DrawBackAmountRowMapper()).stream().findFirst().orElse(null);
    }

    public DrawbackOrderDetailInfo getDrawbackOrderDetail(Integer id) {
        final String sql = String.format("select order_id,drawback_type,drawback_reason,drawback_detail,drawback_pay_price,drawback_yue ,drawback_yongjin ,drawback_imgs,approve_user_id ,approve_comment from %s where order_id = ?",  VarProperties.ORDER_DRAWBACK);
        return jdbcTemplate.query(sql, new Object[]{id}, new DrawbackOrderDetailInfo.DrawbackOrderDetailInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public List<OrderListItemInfo> getBuyerOrderList(int userId, OrderController.BuyerOrderStatus orderType, int pageSize, int pageNum) {
        String whereStr = "";
        switch (orderType){
            case ALL:
                whereStr = "and drawback_status = 'NONE' ";
                break;
            case WAIT_PAY:
                whereStr = " and drawback_status = 'NONE' and where status = 'WAIT_PAY'";
                break;
            case WAIT_SEND:
                whereStr = " and drawback_status = 'NONE' and where status = 'WAIT_SEND'";
                break;
            case WAIT_RECEIVE:
                whereStr = " and drawback_status = 'NONE' and where status = 'WAIT_RECEIVE'";
                break;
            case WAIT_COMMENT:
                whereStr = " and drawback_status = 'NONE' and where status = 'WAIT_COMMENT'";
                break;
            case DRAWBACK:
                whereStr = " and where drawback_status != '" + OrderController.DrawbackStatus.NONE.toString() + "'";
                break;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, message,  jianhuo_status , has_fahuo from %s where user_id = ?  %s order by id desc limit ?, ?", VarProperties.ORDER, whereStr);
        return jdbcTemplate.query(sql, new Object[]{userId, startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public List<OrderListItemInfo.OrderItemsForListPage> getOrderItemsForListPage(List<Integer> orderids) {
        final String sql = String.format("select order_id,product_profile_img from %s where order_id in (?)", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderids}, new OrderListItemInfo.OrderItemsForListPageRowMapper());
    }

    public OrderDetailInfo getOrderDetail(String orderNum) {
        String sql = String.format("select id,order_num,address_detail,address_contract,yongjin_code,status,total_price,use_yongjin,use_yue,need_pay_money,had_pay_money,chajia_status,chajia_price,chajia_use_yongjin,chajia_use_yue,chajia_need_pay_money,chajia_had_pay_money,message,jianhuoyuan_id,jianhuo_status,has_fahuo,created_time from %s where order_num = ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderNum}, new OrderDetailInfo.OrderDetailInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public List<OrderDetailItemInfo> getOrderDetailItem(Integer id) {
        final String sql = String.format( "select order_id,product_id,product_name,product_profile_img,product_size,product_cnt,product_total_price,product_total_price,product_sanzhuang,chajia_total_weight,chajia_total_price,jianhuo_success,jianhuo_time from %s where order_id = ? ", VarProperties.ORDERS_ITEM);
        return jdbcTemplate.query(sql, new Object[]{id}, new OrderDetailItemInfo.OrderDetailItemInfoRowMapper());
    }

    public List<OrderListItemInfo> getAdminFahuoOrderList(OrderAdminController.AdminOrderStatus orderType, int pageSize, int pageNum) {
        String whereStr = "";
        switch (orderType){
            case ALL:
                break;
            case NOT_SEND:
                whereStr = "  and where has_fahuo = false";
                break;
            case HAVE_SEND:
                whereStr = " and where has_fahuo = true";
                break;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, message,  jianhuo_status , has_fahuo from %s where drawback_status = 'NONE' %s order by id desc limit ?, ?", VarProperties.ORDER, whereStr);
        return jdbcTemplate.query(sql, new Object[]{startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public void fahuo(int userId, String orderNum) {
        final String sql = String.format("update %s set has_fahuo = true where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{orderNum});
    }

    public void adminApproveDrawback(int approveUserId, String orderNum, OrderController.DrawbackStatus actionType, String attach) {
        final String sql = String.format("update %s set drawback_status = ? , approve_user_id = ?, approve_comment= ?, where order_num = ? ", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{actionType.toString(), approveUserId, attach, orderNum});
    }

    public void updateChajiaOrder(Integer orderID, ChaJiaOrderItemRequest chajia) {
        final String sql = String.format("update %s set chajia_total_weight = ?, chajia_total_price = ? where order_id = ? and id = ?", VarProperties.ORDERS_ITEM);
        jdbcTemplate.update(sql, new Object[]{chajia.getChajiaTotalWeight(), chajia.getChajiaTotalPrice(), orderID, chajia.getId()});
    }

    public void startJianhuo(int userId, Integer orderId) {
        final String sql = String.format("update %s set jianhuoyuan_id =? and jianhuo_status = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{userId, OrderAdminController.JianHYOrderStatus.ING_JIANHUO, orderId});
    }

    public void finishJianhuo(int userId, Integer orderId) {
        final String sql = String.format("update %s set jianhuoyuan_id =? and jianhuo_status = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{userId, OrderAdminController.JianHYOrderStatus.HAD_JIANHUO, orderId});
    }

    public void finishJianhuoItem(Integer id, Integer orderItemId) {
        final String sql = String.format("update %s set jianhuo_success = true, jianhuo_time = now() where order_id = ? and id = ?", VarProperties.ORDERS_ITEM);
        jdbcTemplate.update(sql, new Object[]{id, orderItemId});
    }

    public List<OrderListItemInfo> getDrawbackApproveList(OrderController.DrawbackStatus orderType, int pageSize, int pageNum) {
        String whereStr = "";
        switch (orderType){
            case ALL:
                whereStr = " and drawback_status != 'NONE' ";
                break;
            case WAIT_APPROVE:
                whereStr = " and drawback_status = 'WAIT_APPROVE' ";
                break;
            case APPROVE_PASS:
                whereStr = " and drawback_status = 'APPROVE_PASS'";
                break;
            case APPROVE_REJECT:
                whereStr = " and drawback_status = 'APPROVE_REJECT' ";
                break;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, message,  jianhuo_status , has_fahuo from %s where 1=1  %s order by id desc limit ?, ?", VarProperties.ORDER, whereStr);
        return jdbcTemplate.query(sql, new Object[]{ startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public List<OrderListItemInfo> getOrderListForJianHuoyuan(OrderAdminController.JianHYOrderStatus orderType, int pageSize, int pageNum) {
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, message,  jianhuo_status , has_fahuo from %s where jianhuo_status = ? order by id desc limit ?, ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderType.toString(),  startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public List<SimpleOrderItem> getSimpleOrderItem(List<Integer> ids) {
        final String sql = String.format("select id,product_name,product_profile_img,product_size from %s where id in (?)", VarProperties.ORDERS_ITEM);
        return jdbcTemplate.query(sql, new Object[]{ids}, new SimpleOrderItem.SimpleOrderItemRowMapper());
    }
}