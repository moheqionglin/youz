package com.sm.dao.dao;

import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import com.sm.dao.domain.Tuangou;
import com.sm.message.order.*;
import com.sm.message.tuangou.TuangouOrderInfo;
import com.sm.utils.SmUtil;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.sm.controller.OrderAdminController.*;
import static com.sm.controller.OrderAdminController.TuangouType.*;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 21:03
 */
@Component
public class OrderDao {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ConfigDao configDao;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;




    public Integer createOrder(CreateOrderInfo order) {
        final String sql = String.format("insert into %s (order_num, user_id, address_id, address_detail ,address_contract , yongjin_code , status ," +
                "    total_cost_price,total_price ,use_yongjin ,use_yue , need_pay_money , had_pay_money ,message,yongjin_base_price, delivery_fee," +
                "jianhuo_status,tuangou_mod,tuangou_drawback_amount,tuangou_amount) values(" +
                ":order_num, :user_id, :address_id, :address_detail ,:address_contract , :yongjin_code , :status, " +
                "    :total_cost_price,:total_price ,:use_yongjin ,:use_yue , :need_pay_money , :had_pay_money ," +
                ":message,:yongjin_base_price, :delivery_fee,:jianhuo_status, :tuangou_mod, :tuangou_drawback_amount, :tuangou_amount)", VarProperties.ORDER);
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
        mapSqlParameterSource.addValue("yongjin_base_price", order.getYongjinBasePrice());
        mapSqlParameterSource.addValue("delivery_fee", order.getDeliveryFee());
        mapSqlParameterSource.addValue("jianhuo_status", order.getJianhuoStatus());
        mapSqlParameterSource.addValue("tuangou_mod", order.getTuangouMod());
        mapSqlParameterSource.addValue("tuangou_drawback_amount", order.getTuangouDrawbackAmount());
        mapSqlParameterSource.addValue("tuangou_amount", order.getTuangouAmount());
        namedParameterJdbcTemplate.update(sql, mapSqlParameterSource, keyHolder);
        return keyHolder.getKey().intValue();
    }


    public void createOrderItems(Integer id, List<CreateOrderItemInfo> collect) {

        final String sql = String.format("insert into %s(order_id ,product_id,product_name ,product_profile_img , product_size , product_cnt ,product_total_price, product_unit_price , product_sanzhuang,product_total_tuangou_price,product_total_cost_price) " +
                "values(:order_id ,:product_id,:product_name ,:product_profile_img , :product_size , :product_cnt ,:product_total_price, :product_unit_price , :product_sanzhuang,:product_total_tuangou_price,:product_total_cost_price)", VarProperties.ORDERS_ITEM);

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
            pams.addValue("product_total_tuangou_price", ci.getProductTotalTuangouPrice());
            pams.addValue("product_total_cost_price", ci.getProductTotalCostPrice());
            pss[i] = pams;
        }
        namedParameterJdbcTemplate.batchUpdate(sql, pss);
    }

    public void actionOrderStatus(String orderNum, OrderController.BuyerOrderStatus actionType) {
        final String sql = String.format("update %s set status = ? where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{actionType.toString(), orderNum});

    }

    public void actionTotalOrderDrawbackStatus(String orderNum) {
        final String sql = String.format("update %s set last_status = status, status = ? where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{OrderController.BuyerOrderStatus.DRAWBACK.toString(), orderNum});
    }
    public Integer creteDrawbackOrder( DrawbackRequest drawbackRequest,  DrawBackAmount drawBackAmount) {
        Integer order_id = drawBackAmount.getOrderId();
        List<Integer> orderItemIds = new ArrayList<>();
        if(isDrawbackTotalOrder(drawbackRequest.getOrderItemId())){
            try{
                final String itemIdsSql = String.format("select id from %s where order_id = ?", VarProperties.ORDERS_ITEM);
                orderItemIds = jdbcTemplate.queryForList(itemIdsSql, new Object[]{order_id}, Integer.class);
            }catch (Exception e){
                log.error("get order items error for order id " + order_id);
                throw e;
            }
        }else{
            final String existSql = String.format("select count(1) from %s where id = ?", VarProperties.ORDERS_ITEM);
            Long aLong = jdbcTemplate.queryForObject(existSql, new Object[]{drawbackRequest.getOrderItemId()}, Long.class);
            if(aLong >= 0){
                orderItemIds.add(drawbackRequest.getOrderItemId());
            }
        }
        if(orderItemIds.isEmpty()){
            return -1;
        }

        return this.saveDrawback(order_id, orderItemIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")), drawbackRequest.getType(),
                drawbackRequest.getReason(), drawbackRequest.getDetail(), drawBackAmount.getDisplayTotalAmount(), drawBackAmount.getDisplayTotalYue(),
                drawBackAmount.getDisplayTotalYongjin(), drawbackRequest == null ? "" : drawbackRequest.getImages().stream().collect(Collectors.joining(" | ")),
                drawBackAmount.getDisplayOrderAmount(), drawBackAmount.getDisplayChajiaAmount(),isDrawbackTotalOrder(drawbackRequest.getOrderItemId()),
                drawBackAmount.isDrawbackDeliveryFee() ? drawBackAmount.getDeliveryFee() : BigDecimal.ZERO, false);
    }


    public Integer saveDrawback(Integer order_id, String collect, String type, String reason, String detail, BigDecimal displayTotalAmount,
                                 BigDecimal displayTotalYue, BigDecimal displayTotalYongjin, String imgs, BigDecimal displayOrderAmount,
                                 BigDecimal displayChajiaAmount, boolean drawbackTotalOrder, BigDecimal bigDecimal, boolean drawbackTuangou) {
        final String sql = String.format("insert into %s(order_id, order_item_ids,drawback_type, drawback_reason, drawback_detail, drawback_pay_price, drawback_yue, drawback_yongjin, drawback_imgs,  drawback_amount ,chajia_drawback_amount, drawback_total_order, delivery_fee,drawback_tuangou) " +
                "values (:order_id, :order_item_ids, :drawback_type, :drawback_reason, :drawback_detail, :drawback_pay_price, :drawback_yue, :drawback_yongjin, :drawback_imgs, :drawback_amount, :chajia_drawback_amount, :drawback_total_order, :delivery_fee, :drawback_tuangou)", VarProperties.ORDER_DRAWBACK);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource pams = new MapSqlParameterSource();
        pams.addValue("order_id",  order_id);
        pams.addValue("order_item_ids", collect);
        pams.addValue("drawback_type", type);
        pams.addValue("drawback_reason", reason);
        pams.addValue("drawback_detail", detail);
        //总退款 = 订单退款 + 差价退款
        pams.addValue("drawback_pay_price", displayTotalAmount);
        pams.addValue("drawback_yue", displayTotalYue);
        pams.addValue("drawback_yongjin", displayTotalYongjin);
        pams.addValue("drawback_imgs", imgs);
        pams.addValue("drawback_amount", displayOrderAmount);
        pams.addValue("chajia_drawback_amount", displayChajiaAmount);
        pams.addValue("drawback_total_order", drawbackTotalOrder);
        pams.addValue("delivery_fee", bigDecimal);
        pams.addValue("drawback_tuangou", drawbackTuangou);
        namedParameterJdbcTemplate.update(sql, pams, keyHolder);
        return keyHolder.getKey().intValue();
    }



    private boolean isDrawbackTotalOrder(Integer itemId) {
        return itemId == null || itemId <= 0;
    }

    public SimpleOrder getSimpleOrder(String orderNum){
        final String sql = String.format("select id,user_id,status,yongjin_base_price,order_num ,yongjin_code,jianhuoyuan_id, use_yongjin,had_pay_money,chajia_had_pay_money,use_yue,chajia_need_pay_money,chajia_status,need_pay_money,jianhuo_status,total_cost_price,total_price,tuangou_drawback_amount,tuangou_mod,tuangou_id,tuangou_drawback_status from %s where order_num = ?", VarProperties.ORDER);
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
        final String sql = String.format("select total_price, use_yongjin ,use_yue , need_pay_money, had_pay_money , chajia_status,chajia_price,chajia_use_yongjin ,chajia_use_yue , chajia_need_pay_money , chajia_had_pay_money,delivery_fee from %s where order_num = ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderNum}, new DrawBackAmount.DrawBackAmountRowMapper()).stream().findFirst().orElse(null);
    }

    /**
     * orderItemId != null 的时候返回单独的退款商品
     * 否则表明整单取消，返回整个商品
     * @param orderId
     * @param orderItemId
     * @return
     */
    public DrawbackOrderDetailInfo getDrawbackOrderDetail(Integer orderId, Integer orderItemId) {
        final String sql = String.format("select order_id,drawback_type,drawback_amount,chajia_drawback_amount,drawback_reason,drawback_detail,drawback_pay_price,drawback_yue ,drawback_yongjin ,drawback_imgs,approve_user_id ,approve_comment ,created_time,drawback_total_order,d_status,order_item_ids,delivery_fee from %s where order_id = ?",  VarProperties.ORDER_DRAWBACK);
        List<DrawbackOrderDetailInfo> details = jdbcTemplate.query(sql, new Object[]{orderId}, new DrawbackOrderDetailInfo.DrawbackOrderDetailInfoRowMapper());
        if(isDrawbackTotalOrder(orderItemId)){
            return details.stream().filter(item -> item.isDrawbackTotalOrder()).findFirst().orElse(null);
        }
        return details.stream().filter(item -> item.getOrderItemIds().size() == 1 && item.getOrderItemIds().get(0).equals(orderItemId)).findFirst().orElse(null);
    }

    public List<DrawbackOrderDetailInfo> getAllDrawbackOrderDetailByOrderId(Integer orderId) {
        final String sql = String.format("select order_id,drawback_type,drawback_reason,drawback_detail,drawback_pay_price,drawback_yue ,drawback_yongjin ,drawback_amount,chajia_drawback_amount,drawback_imgs,approve_user_id ,approve_comment ,created_time, d_status from %s where order_id = ?",  VarProperties.ORDER_DRAWBACK);
        return jdbcTemplate.query(sql, new Object[]{orderId}, new DrawbackOrderDetailInfo.DrawbackOrderDetailInfoRowMapper());
    }

    public List<OrderListItemInfo> getBuyerOrderList(int userId, OrderController.BuyerOrderStatus orderType, int pageSize, int pageNum) {
        int startIndex = (pageNum - 1) * pageSize;
        if(OrderController.BuyerOrderStatus.DRAWBACK.equals(orderType)){
            return getDrawbackApproveList(OrderController.DrawbackStatus.ALL ,userId, pageSize, pageNum,  ALL);
        }

        String whereStr = "";
        switch (orderType){
            case ALL:
                whereStr = "and status <> 'DRAWBACK' ";
                break;
            case WAIT_PAY:
                whereStr = " and ((status = 'WAIT_PAY' and  now() < DATE_ADD(created_time, INTERVAL 15 MINUTE )) or (chajia_status='WAIT_PAY' and status != 'WAIT_SEND')) ";
                break;
            case WAIT_SEND:
                whereStr = " and  status = 'WAIT_SEND'";
                break;
            case WAIT_RECEIVE:
                whereStr = " and status = 'WAIT_RECEIVE' AND chajia_status != 'WAIT_PAY'";
                break;
            case WAIT_COMMENT:
                whereStr = " and status = 'WAIT_COMMENT' AND chajia_status != 'WAIT_PAY'";
                break;
        }

        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , created_time, status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, message,  jianhuo_status , has_fahuo from %s where user_id = ?  %s order by id desc limit ?, ?", VarProperties.ORDER, whereStr);
        return jdbcTemplate.query(sql, new Object[]{userId, startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public List<OrderListItemInfo.OrderItemsForListPage> getOrderItemsForListPage(List<Integer> orderids) {
        if(orderids == null || orderids.isEmpty()){
            return new ArrayList<>();
        }
        final String sql = String.format("select order_id,product_profile_img from %s where order_id in (:ids)", VarProperties.ORDERS_ITEM);
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", orderids), new OrderListItemInfo.OrderItemsForListPageRowMapper());
    }

    public OrderDetailInfo getOrderDetail(String orderNum) {
        String sql = String.format("select id, user_id, order_num,address_detail,address_contract,yongjin_code,status,total_price,use_yongjin,use_yue,need_pay_money,had_pay_money,chajia_status,chajia_price,chajia_use_yongjin,chajia_use_yue,chajia_need_pay_money,chajia_had_pay_money,message,jianhuoyuan_id,jianhuo_status,has_fahuo,created_time, delivery_fee,tuangou_id,tuangou_mod,tuangou_drawback_amount,tuangou_drawback_status,tuangou_amount,address_id from %s where order_num = ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{orderNum}, new OrderDetailInfo.OrderDetailInfoRowMapper()).stream().findFirst().orElse(null);
    }

    public List<OrderDetailItemInfo> getOrderDetailItem(Integer id) {
        final String sql = String.format( "select id,order_id,product_id,product_name,product_profile_img,product_size,product_cnt,product_total_price,product_total_price,product_sanzhuang,chajia_total_weight,chajia_total_price,jianhuo_success,jianhuo_time,product_total_tuangou_price,product_total_cost_price from %s where order_id = ? ", VarProperties.ORDERS_ITEM);
        return jdbcTemplate.query(sql, new Object[]{id}, new OrderDetailItemInfo.OrderDetailItemInfoRowMapper());
    }
    public List<OrderDetailItemInfo> getOrderDetailItemByItemId(Integer OrderId, Integer itemId) {
        final String sql = String.format( "select id,order_id,product_id,product_name,product_profile_img,product_size,product_cnt,product_total_price,product_total_price,product_sanzhuang,chajia_total_weight,chajia_total_price,jianhuo_success,jianhuo_time,product_total_tuangou_price,product_total_cost_price from %s where id = ? and order_id = ?", VarProperties.ORDERS_ITEM);
        return jdbcTemplate.query(sql, new Object[]{itemId, OrderId}, new OrderDetailItemInfo.OrderDetailItemInfoRowMapper());
    }
    public OrderDetailItemInfo getOrderDetailItemByOrderItemId(Integer orderItemId){
        final String sql = String.format( "select id,order_id,product_id,product_name,product_profile_img,product_size,product_cnt,product_total_price,product_total_price,product_sanzhuang,chajia_total_weight,chajia_total_price,jianhuo_success,jianhuo_time,product_total_tuangou_price,product_total_cost_price from %s where id = ? ", VarProperties.ORDERS_ITEM);
        return jdbcTemplate.query(sql, new Object[]{orderItemId}, new OrderDetailItemInfo.OrderDetailItemInfoRowMapper()).stream().findAny().orElse(null);
    }
    public List<OrderListItemInfo> getAdminFahuoOrderList(AdminOrderStatus orderType, int pageSize, int pageNum) {
        String whereStr = "";
        switch (orderType){
            case ALL:
                break;
            case NOT_SEND:
                whereStr = "  and  status = 'WAIT_SEND'";
                break;
            case HAVE_SEND:
                whereStr = " and   has_fahuo = true ";
                break;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, message,  jianhuo_status , has_fahuo,created_time from %s where status <> 'DRAWBACK' %s order by id desc limit ?, ?", VarProperties.ORDER, whereStr);
        return jdbcTemplate.query(sql, new Object[]{startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public void fahuo(int userId, String orderNum) {
        final String sql = String.format("update %s set has_fahuo = true, status='WAIT_RECEIVE' where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{orderNum});
    }

    @Transactional
    public void adminApproveDrawback(int approveUserId, Integer orderId, String orderNum, Integer orderItemId, OrderController.DrawbackStatus actionType, String attach) {
        if(isDrawbackTotalOrder(orderItemId) && OrderController.DrawbackStatus.APPROVE_REJECT.toString().equals(actionType.toString())){
            final String sql1 = String.format("update %s set status = last_status where order_num= ?", VarProperties.ORDER);
            jdbcTemplate.update(sql1, new Object[]{orderNum});
        }

        final String sql = String.format("update %s set d_status = ?, approve_user_id = ?, approve_comment= ? where order_id = ? ", VarProperties.ORDER_DRAWBACK);
        jdbcTemplate.update(sql, new Object[]{ actionType.toString(), approveUserId, attach, orderId});
    }

    public void updateChajiaOrder(Integer orderID, ChaJiaOrderItemRequest chajia) {
        final String sql = String.format("update %s set chajia_total_weight = ?, chajia_total_price = ? where order_id = ? and id = ?", VarProperties.ORDERS_ITEM);
        jdbcTemplate.update(sql, new Object[]{chajia.getChajiaTotalWeight(), chajia.getChajiaTotalPrice(), orderID, chajia.getId()});

    }

    public void startJianhuo(int userId, Integer orderId) {
        final String sql = String.format("update %s set jianhuoyuan_id =? , jianhuo_status = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{userId, JianHYOrderStatus.ING_JIANHUO.toString(), orderId});
    }

    public void finishJianhuo(int userId, Integer orderId) {
        final String sql = String.format("update %s set jianhuoyuan_id =? , jianhuo_status = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{userId, JianHYOrderStatus.HAD_JIANHUO.toString(), orderId});
    }

    public void finishJianhuoWithChajia(int userId, Integer orderId, BigDecimal chajiaprice, BigDecimal chajiaCost) {
        final String sql = String.format("update %s set jianhuoyuan_id =? , jianhuo_status = ?,chajia_status = ?,chajia_price =?, total_cost_price = total_cost_price + ?, chajia_need_pay_money=? where id = ?", VarProperties.ORDER);

        jdbcTemplate.update(sql, new Object[]{userId, JianHYOrderStatus.HAD_JIANHUO.toString(),
                chajiaprice.compareTo(BigDecimal.ZERO) > 0 ? ChaJiaOrderStatus.WAIT_PAY.toString(): ChaJiaOrderStatus.HAD_PAY.toString(),
                chajiaprice, chajiaCost, chajiaprice, orderId});
    }
    public void adminfinishCalcChajia(Integer orderId, BigDecimal chajiaprice) {
        final String sql = String.format("update %s set chajia_status = ?,chajia_price =?,chajia_need_pay_money=? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{
                chajiaprice.compareTo(BigDecimal.ZERO) > 0 ? ChaJiaOrderStatus.WAIT_PAY.toString(): ChaJiaOrderStatus.HAD_PAY.toString() , chajiaprice, chajiaprice, orderId});
    }
    public void finishJianhuoItem(Integer id, Integer orderItemId) {
        final String sql = String.format("update %s set jianhuo_success = true, jianhuo_time = now() where order_id = ? and id = ?", VarProperties.ORDERS_ITEM);
        jdbcTemplate.update(sql, new Object[]{id, orderItemId});
    }

    public List<OrderListItemInfo> getDrawbackApproveList(OrderController.DrawbackStatus orderType, Integer buyerID, int pageSize, int pageNum, TuangouType tuangouType) {
        String userWhere = " ";
        if(buyerID != null){
            userWhere = " AND t1.user_id = " + buyerID + " ";
        }
        String whereStr = "";
        switch (orderType){
            case ALL:
                whereStr = " ";
                break;
            case WAIT_APPROVE:
                whereStr = " and t2.d_status = 'WAIT_APPROVE' ";
                break;
            case APPROVE_PASS:
                whereStr = " and t2.d_status = 'APPROVE_PASS' ";
                break;
            case APPROVE_REJECT:
                whereStr = " and t2.d_status = 'APPROVE_REJECT' ";
                break;
        }
        switch (tuangouType){
            case ALL:
                whereStr += " ";
                break;
            case TUANGOU:
                whereStr += " and drawback_tuangou = 1 ";
                break;
            case NONTUANGOU:
                whereStr += " and drawback_tuangou = 0 ";
                break;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select t1.id as id,t1.order_num as order_num, t1.user_id  as user_id, address_id, address_detail, address_contract, t1.created_time as created_time,  t1.status  as status, t1.total_price as total_price, chajia_status, chajia_price,  drawback_reason, chajia_need_pay_money, chajia_had_pay_money, message, jianhuo_status, has_fahuo, t2.d_status as d_status, order_item_ids, t2.drawback_total_order, t2.drawback_tuangou, (t2.drawback_pay_price + t2.drawback_yue + t2.drawback_yongjin) as item_price from %s t2 left join %s t1 on t1.id = t2.order_id where 1=1 %s %s order by t1.id desc limit ?, ?", VarProperties.ORDER_DRAWBACK, VarProperties.ORDER, userWhere, whereStr);
        List<OrderListItemInfo> rst = jdbcTemplate.query(sql, new Object[]{ startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());

        final String imgSql = String.format("select product_profile_img from %s where id in (:ids)", VarProperties.ORDERS_ITEM);

        rst.stream().forEach(it -> {
            List<Integer> drawbackItemIds = it.getDrawbackItemIds();
            if(drawbackItemIds != null && !drawbackItemIds.isEmpty()){
                if(it.isDrawbackTuangou()){//团购退款
                    it.setProductImges(Arrays.asList("https://img.suimeikeji.com/chengtuantuichajia.png"));
                    it.setDrawbackTuangouChajia(true);
                }else{
                    List<String> imgs = namedParameterJdbcTemplate.queryForList(imgSql, Collections.singletonMap("ids", drawbackItemIds), String.class);
                    it.setProductImges(imgs);
                }

            }
        });
        return rst;
    }

    public List<OrderListItemInfo> getOrderListForJianHuoyuan(Integer userid, JianHYOrderStatus orderType, int pageSize, int pageNum) {
        int startIndex = (pageNum - 1) * pageSize;
        String whereSql = "";
        switch (orderType){
            case NOT_JIANHUO:
                whereSql = " and status = 'WAIT_SEND' ";
                break;
            default:
                whereSql = "and jianhuoyuan_id = " + userid + " ";
                break;
        }
        final String sql = String.format("select id ,order_num,user_id ,address_id ,address_detail ,address_contract , status, total_price ,chajia_status,chajia_price, chajia_need_pay_money, chajia_had_pay_money, created_time, message,  jianhuo_status , has_fahuo from %s where jianhuo_status = ? and status <> 'DRAWBACK' %s order by id desc limit ?, ?", VarProperties.ORDER, whereSql);
        return jdbcTemplate.query(sql, new Object[]{orderType.toString(),  startIndex, pageSize}, new OrderListItemInfo.OrderListItemInfoRowMapper());
    }

    public List<SimpleOrderItem> getSimpleOrderItem(List<Integer> ids) {
        if(ids == null || ids.isEmpty()){
            return new ArrayList<>(1);
        }
        final String sql = String.format("select id,product_name,product_profile_img,product_size,product_sanzhuang from %s where id in (:ids)", VarProperties.ORDERS_ITEM);
        return namedParameterJdbcTemplate.query(sql, Collections.singletonMap("ids", ids), new SimpleOrderItem.SimpleOrderItemRowMapper());
    }
    public SimpleOrderItem getSimpleOrderItem(Integer id) {
        if(id == null ){
            return null;
        }
        final String sql = String.format("select id,product_name,product_profile_img,product_size,product_sanzhuang from %s where id =?", VarProperties.ORDERS_ITEM);
        return jdbcTemplate.query(sql, new Object[]{id}, new SimpleOrderItem.SimpleOrderItemRowMapper()).stream().findFirst().orElse(null);
    }

    public void finishOrder(String orderNum) {
        final String sql = String.format("update %s set status = ? where order_num = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{OrderController.BuyerOrderStatus.FINISH.toString(), orderNum });
    }

    @Transactional
    public void cancelDrawback(String orderNum, Integer orderId, Integer orderItemID) {
        final String sql = String.format("update %s set status = last_status where order_num =?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{OrderController.DrawbackStatus.NONE.toString(), orderNum});
        if(isDrawbackTotalOrder(orderItemID)){
            final String sql1 = String.format("delete from %s where order_id = ? and drawback_total_order = 1", VarProperties.ORDER_DRAWBACK);
            jdbcTemplate.update(sql1, new Object[]{orderId});
        }else{
            final String sql1 = String.format("delete from %s where order_id = ? and drawback_total_order = 0 and order_item_ids = ?", VarProperties.ORDER_DRAWBACK);
            jdbcTemplate.update(sql1, new Object[]{orderId, orderItemID});
        }

    }

    public void surePayment(Integer id, BigDecimal payAmount, boolean chajia) {
        final String sql = String.format("update %s set status = ? , had_pay_money = ? where id = ? and status = 'WAIT_PAY'", VarProperties.ORDER);
        final String cjsql = String.format("update %s set chajia_status = ? , chajia_had_pay_money = ? where id = ? and  chajia_status = 'WAIT_PAY'", VarProperties.ORDER);
        if(chajia){
            jdbcTemplate.update(cjsql, new Object[]{ChaJiaOrderStatus.HAD_PAY.toString(), payAmount, id});
        }else{
            jdbcTemplate.update(sql, new Object[]{OrderController.BuyerOrderStatus.WAIT_SEND.toString(), payAmount, id});
        }
    }

    public void fillDrawbackNum(String drawbacknum, Integer orderid) {
        final String sql1 = String.format("update %s set drawback_num = ? where order_id= ?", VarProperties.ORDER_DRAWBACK);
        jdbcTemplate.update(sql1, new Object[]{drawbacknum, orderid});

    }

    public int sureDrawbackPayment(BigDecimal refAmnt, String refundNum, String orderNum) {
        String simo = orderNum.replaceAll("CJ", "");
        final String sql = String.format("select id from %s where order_num = ?", VarProperties.ORDER);
        try {
            Integer orderId = jdbcTemplate.queryForObject(sql, new Object[]{simo}, Integer.class);
            if(!orderNum.contains("CJ")){
                final String sql1 = String.format("update %s set drawback_num = ? , drawback_callback = 1 where order_id = ?", VarProperties.ORDER_DRAWBACK);
                jdbcTemplate.update(sql1, new Object[]{refundNum, orderId});
            }else if(orderNum.contains("CJ")){
                final String sql1 = String.format("update %s set chajia_drawback_num = ? , chajia_drawback_callback = 1 where order_id = ?", VarProperties.ORDER_DRAWBACK);
                jdbcTemplate.update(sql1, new Object[]{refundNum, orderId});
            }
            return 1;
        }catch (Exception e){
            return -1;
        }



    }

    public boolean hasWaitPayChajiaOrder(int userID) {
        final String sql = String.format("select count(1) from %s where  chajia_status = 'WAIT_PAY' and status != 'WAIT_SEND' and status != 'DRAWBACK' and user_id = ?", VarProperties.ORDER);
        long count = jdbcTemplate.queryForObject(sql, new Object[]{userID}, Long.class);
        return count > 0;
    }

    /**
     * 归还 超时取消订单的佣金合约
     */
    public void fixCancelTimeoutOrder() {
        final String sql1 = String.format("select user_id, use_yongjin,use_yue from %s where status = 'WAIT_PAY' and  now() > DATE_ADD(created_time, INTERVAL 15 MINUTE )", VarProperties.ORDER);
        try{
            jdbcTemplate.queryForList(sql1).stream().forEach(o -> {
               int userId = Integer.parseInt(o.get("user_id").toString());
               double useYongjin = Double.valueOf(o.get("use_yongjin").toString());
               double useYue = Double.valueOf(o.get("use_yue").toString());
               if(useYongjin >=0 || useYue>=0){
                   final String sql2 = String.format("update users set yongjin = yongjin + ?, amount = amount + ? where id = ", VarProperties.USERS);
                   jdbcTemplate.update(sql2, new Object[]{useYongjin, useYue, userId});
                   log.info("drawback for cancel timeout order, yongjin = {}, yue ={}, userId ={}", useYongjin, useYue, userId);
               }
            });
        }catch (Exception e){
            log.error("process fixCancelTimeoutOrder error ", e);
        }


        final String sql = String.format("update %s set status = 'CANCEL_TIMEOUT' where status = 'WAIT_PAY' and  now() > DATE_ADD(created_time, INTERVAL 15 MINUTE )", VarProperties.ORDER);
        jdbcTemplate.update(sql);
    }

    public long getJianhuoCnt(int userId, JianHYOrderStatus type) {

        String userCon = "";
        switch (type){
            case NOT_JIANHUO:
                userCon = "  and status = 'WAIT_SEND'  ";
                break;
            default:
                userCon = " and jianhuoyuan_id = " + userId;
                break;
        }
        if(JianHYOrderStatus.HAD_JIANHUO.equals(type) ||
                JianHYOrderStatus.ING_JIANHUO.equals(type) ){
            userCon = " and jianhuoyuan_id = " + userId;
        }
        final String sql = String.format("select count(1) from %s where jianhuo_status = ? and status <> 'DRAWBACK' %s ", VarProperties.ORDER, userCon);
        return jdbcTemplate.queryForObject(sql, new Object[]{type.toString()}, Long.class);
    }

    public OrderAllStatusCntInfo countOrderAllStatus(int userId) {
        final String WAIT_PAY = " and  ((status = 'WAIT_PAY' and  now() < DATE_ADD(created_time, INTERVAL 15 MINUTE )) or (chajia_status='WAIT_PAY' and status != 'WAIT_SEND')) ";
        final String WAIT_SEND = " and status = 'WAIT_SEND'";
        final String WAIT_RECEIVE = " and  status = 'WAIT_RECEIVE'";
        final String WAIT_COMMENT = " and status = 'WAIT_COMMENT'";
        final String DRAWBACK = " and   d_status = '" + OrderController.DrawbackStatus.WAIT_APPROVE.toString() + "'";

        OrderAllStatusCntInfo info = new OrderAllStatusCntInfo();
        final String WAIT_PAY_SQL = String.format("select count(1) from %s where user_id = ?  %s ", VarProperties.ORDER, WAIT_PAY);
        final String WAIT_SEND_SQL = String.format("select count(1) from %s where user_id = ?  %s ", VarProperties.ORDER, WAIT_SEND);
        final String WAIT_RECEIVE_SQL = String.format("select count(1) from %s where user_id = ?  %s ", VarProperties.ORDER, WAIT_RECEIVE);
        final String WAIT_COMMENT_SQL = String.format("select count(1) from %s where user_id = ?  %s ", VarProperties.ORDER, WAIT_COMMENT);
        final String DRAWBACK_SQL = String.format("select count(1) from %s t1 left join %s t2 on t1.order_id = t2.id  where t2.user_id = ?  %s ", VarProperties.ORDER_DRAWBACK, VarProperties.ORDER, DRAWBACK);
        final String TIXIAN_SQL = String.format("select count(1) from tixian_approve where approve_status = 'WAIT_APPROVE'");

        info.setWaitPayCnt(jdbcTemplate.queryForObject(WAIT_PAY_SQL, new Object[]{userId}, Long.class).intValue());
        info.setWaitSentCnt(jdbcTemplate.queryForObject(WAIT_SEND_SQL, new Object[]{userId}, Long.class).intValue());
        info.setWaitReceiveCnt(jdbcTemplate.queryForObject(WAIT_RECEIVE_SQL, new Object[]{userId}, Long.class).intValue());
        info.setWaitCommentCnt(jdbcTemplate.queryForObject(WAIT_COMMENT_SQL, new Object[]{userId}, Long.class).intValue());
        info.setDrawbackCnt(jdbcTemplate.queryForObject(DRAWBACK_SQL, new Object[]{userId}, Long.class).intValue());
        info.setWaitTiXianCnt(jdbcTemplate.queryForObject(TIXIAN_SQL, Long.class).intValue());
        return info;
    }

    public int countOrderManagerCnt() {
        final String sql = String.format("select count(1) from %s where status = 'WAIT_SEND' ", VarProperties.ORDER);
        return jdbcTemplate.queryForObject(sql, Long.class).intValue();
    }

    public int countDrawbackManagerCnt() {
        final String sql = String.format("select count(1) from %s where d_status = 'WAIT_APPROVE'", VarProperties.ORDER_DRAWBACK);
        return jdbcTemplate.queryForObject(sql, Long.class).intValue();
    }

    /**
     * 超过两次需要付运费
     * @param userId
     * @return
     */
    public int todayOrderCnt(int userId) {
        final String sql = String.format("select count(1) from %s where user_id = ? and created_time > ? and status != 'CANCEL_TIMEOUT' AND status!= 'CANCEL_MANUAL' AND not (status = 'WAIT_PAY' and  now() > DATE_ADD(created_time, INTERVAL 15 MINUTE ))", VarProperties.ORDER);
        return jdbcTemplate.queryForObject(sql, new Object[]{userId, SmUtil.getTodayYMD() + " 00:00:00"}, Integer.class);
    }

    public boolean needPayDeliveryFee(int userId, BigDecimal amount) {
//        int orderCnt = todayOrderCnt(userId);
//        BigDecimal freeDeliveryFeeOrderAmount = configDao.getFreeDeliveryFeeOrderAmount();
//        return amount.compareTo(freeDeliveryFeeOrderAmount) < 0 && orderCnt >= 2;
        return true;
    }

    public HashMap<Integer, Integer> getPid2StockByOrderId(Integer orderId) {
        final String sql = String.format("select product_id, product_cnt from %s where order_id = ?", VarProperties.ORDERS_ITEM);
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[]{orderId});
        HashMap<Integer, Integer> pid2cnt = new HashMap<>();
        results.stream().forEach(map -> {
            try{
                pid2cnt.put(Integer.valueOf(map.get("product_id").toString()), Integer.valueOf(map.get("product_cnt").toString()));
            }catch (Exception e){

            }
        });
        return pid2cnt;
    }

    public int countFeedbackManagerCnt() {
        final String sql = String.format("select count(1) from %s where had_read = 0", VarProperties.FEEBACK);
        return jdbcTemplate.queryForObject(sql,  Long.class).intValue();

    }

    public void modifyOrderStatus() {
        final String sql = String.format("update %s set status = '"+OrderController.BuyerOrderStatus.FINISH+"' where (status = '"+OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString()+"' or status = '"+OrderController.BuyerOrderStatus.WAIT_COMMENT.toString()+"' ) and chajia_status != '"+ ChaJiaOrderStatus.WAIT_PAY.toString()+"' and modified_time < DATE_SUB(now(),INTERVAL 2 DAY)", VarProperties.ORDER);
        jdbcTemplate.update(sql);
    }

    public List<SimpleOrder> getWaitReceiveOrderStatus() {
        final String sql = String.format("select id,user_id,status,yongjin_base_price,order_num ,yongjin_code,jianhuoyuan_id, use_yongjin,had_pay_money,chajia_had_pay_money,use_yue,chajia_need_pay_money,chajia_status,need_pay_money,jianhuo_status,total_cost_price,total_price,tuangou_drawback_amount,tuangou_mod,tuangou_id,tuangou_drawback_status  from  %s  where (status = '"+OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString()+"' and modified_time < DATE_SUB(now(),INTERVAL 2 DAY)", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new SimpleOrder.SimpleOrderRowMapper());
    }

    public void updateTuangou(int tuangouId, int orderId) {
        final String sql = String.format("update %s set tuangou_id = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{tuangouId, orderId});
    }

    public String getDstatus(Integer orderId, Integer orderItemId) {
        String sql = String.format("select d_status from %s where order_id = ? and drawback_total_order = 1", VarProperties.ORDER_DRAWBACK);
        Object[] parms = new Object[]{orderId};
        if(!isDrawbackTotalOrder(orderItemId)){
            sql = String.format("select d_status from %s where order_id = ? and order_item_ids = ?", VarProperties.ORDER_DRAWBACK);
            parms = new Object[]{orderId, orderItemId.toString()};
        }
        try{
            return jdbcTemplate.queryForObject(sql, parms, String.class);
        }catch (Exception e){
            return null;
        }
    }

    public HashMap<Integer, Boolean> getHadDrawbackItem(Integer orderId) {

        try{
            HashMap<Integer, Boolean> rst = new HashMap<>();
            List<Map<String, Object>> maps = jdbcTemplate.queryForList("select order_item_ids, drawback_total_order from order_drawback where order_id = ? and d_status in ('"+OrderController.DrawbackStatus.WAIT_APPROVE.toString()+"','"+OrderController.DrawbackStatus.APPROVE_PASS.toString()+"') ",
                    new Object[]{orderId});
            for(Map<String, Object> map : maps){
                if(Boolean.valueOf(map.get("drawback_total_order").toString())){
                    Arrays.stream(map.get("order_item_ids").toString().split(",")).forEach(id -> {
                        rst.put(Integer.parseInt(id), true);
                    });
                }else{
                    rst.put(Integer.parseInt(map.get("order_item_ids").toString()), true);
                }
            }
            return rst;
        }catch (Exception e){
            log.error("getHadDrawbackItem error", e);
        }
        return null;
    }

    public List<OrderDetailInfo> getOrdersByTuangouId(int tuangouId) {
        String sql = String.format("select id, user_id, order_num,address_detail,address_contract,yongjin_code,status,total_price,use_yongjin,use_yue,need_pay_money,had_pay_money,chajia_status,chajia_price,chajia_use_yongjin,chajia_use_yue,chajia_need_pay_money,chajia_had_pay_money,message,jianhuoyuan_id,jianhuo_status,has_fahuo,created_time, delivery_fee,tuangou_id,tuangou_mod,tuangou_drawback_amount,tuangou_drawback_status,tuangou_amount from %s where tuangou_id = ?", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new Object[]{tuangouId}, new OrderDetailInfo.OrderDetailInfoRowMapper());
    }

    public void updateJianhuoStatus(Integer id, JianHYOrderStatus notJianhuo) {
        final String sql = String.format("update %s set jianhuo_status = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{notJianhuo.name(), id});
    }

    public List<OrderDetailInfo> queryTuangouModOrderByTuangouId(Integer id) {
        String sql = String.format("select id, user_id, order_num,address_detail,address_contract,yongjin_code,status,total_price,use_yongjin,use_yue,need_pay_money,had_pay_money,chajia_status,chajia_price,chajia_use_yongjin,chajia_use_yue,chajia_need_pay_money,chajia_had_pay_money,message,jianhuoyuan_id,jianhuo_status,has_fahuo,created_time, delivery_fee,tuangou_id,tuangou_mod,tuangou_drawback_amount,tuangou_drawback_status,tuangou_amount from %s where tuangou_id = ? and tuangou_mod = 'TUANGOU'", VarProperties.ORDER);
        return jdbcTemplate.query(sql, new  Object[]{id}, new OrderDetailInfo.OrderDetailInfoRowMapper());
    }

    public void updateTuangouDrawbackStatus(Integer id) {
        final String sql = String.format("update %s set tuangou_drawback_status = ? where id = ?", VarProperties.ORDER);
        jdbcTemplate.update(sql, new Object[]{1, id});
    }

    public List<Integer> queryTuangouId(Integer userId) {
        //查出自己参与的团购单子，订单支付成功，且是 tuangou_mod是 single或者tuangou的单子。
        String sql = String.format("select distinct tuangou_id from %s where tuangou_mod in ('SINGLE','TUANGOU') and user_id = ? and tuangou_id > 0", VarProperties.ORDER);
        return jdbcTemplate.queryForList(sql, new Object[]{userId},Integer.class);
    }

    public List<TuangouOrderInfo> queryTuangouSimpleOrderByTuangouIds(Integer userId, List<Integer> tuangouIDs) {
        final String sql = String.format("select orders.id, orders.tuangou_drawback_amount, orders.tuangou_id, orders.created_time,users.nick_name, users.head_picture from orders left join users on orders.user_id = users.id where orders.user_id = :user_id and tuangou_id in (:ids)", VarProperties.TUANGOU);
        MapSqlParameterSource pams = new MapSqlParameterSource();
        pams.addValue("user_id", userId);
        pams.addValue("ids", tuangouIDs);
        return namedParameterJdbcTemplate.query(sql, pams, new TuangouOrderInfo.TuangouOrderDetailItemInfoRowMapper());
    }

}