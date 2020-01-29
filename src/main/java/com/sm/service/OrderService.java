package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import com.sm.dao.dao.AdminDao;
import com.sm.dao.dao.OrderDao;
import com.sm.message.ResultJson;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.order.*;
import com.sm.message.product.ProductListItem;
import com.sm.message.profile.UserAmountInfo;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 21:03
 */
@Component
public class OrderService {
    private Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;

    @Autowired
    private AdminDao adminDao;
    @Autowired
    private AddressService addressService;

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    /**
     *   5. 删除购物车
     * @param userID
     * @param order
     * @return
     */
    @Transactional
    public ResultJson<String> createOrder(int userID, CreateOrderRequest order) {
        //1. 地址校验
        AddressDetailInfo addressDetail = addressService.getAddressDetail(userID, order.getAddressId());
        if(addressDetail == null){
            return ResultJson.failure(HttpYzCode.ADDRESS_NOT_EXISTS);
        }
        //2. 校验 佣金，和余额
        UserAmountInfo userAmountInfo = userService.getAmount(userID);
        if(userAmountInfo == null ||
                userAmountInfo.getYongjin() == null ||  userAmountInfo.getYongjin().compareTo(order.getUseYongjin()) < 0 ||
                userAmountInfo.getYue() == null || userAmountInfo.getYue().compareTo(order.getUseYue()) < 0){
            return ResultJson.failure(HttpYzCode.YONGJIN_YUE_NOT_ENOUGH);
        }
        //3. 校验库存
        List<CartItemInfo> cartItems = shoppingCartService.getAllCartItems(userID, true);
        cartItems = cartItems.stream().filter(c -> order.getCartIds().contains(c.getId())).collect(Collectors.toList());
        if(cartItems.size() != order.getCartIds().size()){
            return ResultJson.failure(HttpYzCode.PRODUCT_XIAJIA);
        }
        if(cartItems.stream().filter(c -> !c.getProduct().isShowAble()).count() > 0){
            return ResultJson.failure(HttpYzCode.PRODUCT_XIAJIA);
        }
        //4. 校验产品下架
        if(cartItems.stream().filter(c -> c.getProduct().getStock() < c.getProductCnt()).count() > 0){
            return ResultJson.failure(HttpYzCode.EXCEED_STOCK);
        }

        CreateOrderInfo createOrderInfo = new CreateOrderInfo();

        createOrderInfo.setOrderNum(SmUtil.getOrderCode());
        createOrderInfo.setUserId(userID);
        createOrderInfo.setAddressId(addressDetail.getId());
        createOrderInfo.setAddressDetail(addressDetail.getShippingAddress() + " " + addressDetail.getShippingAddressDetails());
        createOrderInfo.setAddressContract(addressDetail.getLinkPerson() + " " + addressDetail.getPhone());
        createOrderInfo.setStatus(OrderController.BuyerOrderStatus.WAIT_PAY.toString());
        createOrderInfo.setTotalCostPrice(ServiceUtil.calcCartTotalCost(cartItems));
        createOrderInfo.setTotalPrice(ServiceUtil.calcCartTotalPrice(cartItems));
        createOrderInfo.setUseYongjin(order.getUseYongjin());
        createOrderInfo.setUseYue(order.getUseYue());
        if((createOrderInfo.getUseYongjin().add(createOrderInfo.getUseYue())).compareTo(createOrderInfo.getTotalPrice()) > 0){
            return ResultJson.failure(HttpYzCode.YONGJIN_YUE_PRICE_ERROR);
        }
        BigDecimal subtract = createOrderInfo.getTotalCostPrice()
                .subtract(createOrderInfo.getUseYongjin())
                .subtract(createOrderInfo.getUseYue()).setScale(2, RoundingMode.UP);
        if(subtract.compareTo(BigDecimal.ZERO) <= 0){
            createOrderInfo.setNeedPayMoney(BigDecimal.ZERO);
            createOrderInfo.setStatus(OrderController.BuyerOrderStatus.WAIT_SEND.toString());
        }else{
            createOrderInfo.setNeedPayMoney(subtract);
        }
        createOrderInfo.setHadPayMoney(BigDecimal.ZERO);
        createOrderInfo.setYongjinCode(order.getYongjinCode());
        createOrderInfo.setMessage(order.getMessage());

        Integer id = orderDao.createOrder(createOrderInfo);

        List<CreateOrderItemInfo> collect = cartItems.stream().map(c -> {
            CreateOrderItemInfo ci = new CreateOrderItemInfo();
            ProductListItem product = c.getProduct();
            ci.setOrderId(id);
            ci.setProductId(product.getId());
            ci.setProductName(product.getName());
            ci.setProductProfileImg(product.getProfileImg());
            ci.setProductSize(product.getSize());
            ci.setProductCnt(c.getProductCnt());
            ci.setProductTotalPrice(ServiceUtil.calcCartItemPrice(c));
            ci.setProductUnitPrice(ServiceUtil.calcUnitPrice(c));
            ci.setProductSanzhuang(product.isSanzhung());
            return ci;
        }).collect(Collectors.toList());
        orderDao.createOrderItems(id, collect);

        //产品销量加加
        productService.addSalesCount(collect.stream().map(o -> o.getProductId()).collect(Collectors.toList()));

        if(StringUtils.isNoneBlank(createOrderInfo.getYongjinCode())){
            //佣金计算
            BigDecimal yongJinPercent = adminDao.getYongJinPercent();

            if(StringUtils.isNoneBlank(createOrderInfo.getYongjinCode()) && yongJinPercent.compareTo(BigDecimal.ZERO) > 0 && yongJinPercent.compareTo(BigDecimal.ONE) < 0){
                BigDecimal total = ServiceUtil.calcCartTotalPriceWithoutZhuanqu(cartItems);
                adminDao.updateYongjin(createOrderInfo.getYongjinCode(), total.multiply(yongJinPercent).setScale(2, RoundingMode.DOWN));
                logger.info("Update yongjin for order {}/{}, order total {}, yongjin = [{} * {}], ", id, createOrderInfo.getOrderNum(), createOrderInfo.getTotalCostPrice(), total, yongJinPercent);
            }
        }


        return ResultJson.ok(createOrderInfo.getOrderNum());
    }

    public SimpleOrder getSimpleOrder(String orderNum){
        return orderDao.getSimpleOrder(orderNum);
    }
    public ResultJson actionOrder(int userId, String orderNum, OrderController.ActionOrderType actionType) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!(OrderController.BuyerOrderStatus.WAIT_PAY.toString().equalsIgnoreCase(simpleOrder.getStatus())
                && OrderController.ActionOrderType.CANCEL_MANUAL.equals(actionType))
        && !(OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString().equalsIgnoreCase(simpleOrder.getStatus())
                && OrderController.ActionOrderType.RECEIVE.equals(actionType))){
            return ResultJson.failure(HttpYzCode.ORDER_STATUS_ERROR);
        }
        OrderController.BuyerOrderStatus bysta = null;
        if(OrderController.ActionOrderType.RECEIVE.equals(actionType)){
            bysta = OrderController.BuyerOrderStatus.WAIT_COMMENT;
        }else {
            bysta = OrderController.BuyerOrderStatus.CANCEL_MANUAL;
        }

        orderDao.actionOrderStatus(orderNum, bysta);
        return ResultJson.ok();
    }

    public ResultJson drawbackOrder(int userId, DrawbackRequest drawbackRequest) {
        String orderNum = drawbackRequest.getOrderNum();
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!(OrderController.BuyerOrderStatus.WAIT_SEND.toString().equalsIgnoreCase(simpleOrder.getStatus())
         || OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString().equalsIgnoreCase(simpleOrder.getStatus())
         || OrderController.BuyerOrderStatus.WAIT_COMMENT.toString().equalsIgnoreCase(simpleOrder.getStatus()))){
            return ResultJson.failure(HttpYzCode.ORDER_STATUS_ERROR);
        }
        if(!OrderController.DrawbackStatus.NONE.toString().equals(simpleOrder.getDrawbackStatus())){
            return ResultJson.failure(HttpYzCode.ORDER_DRAWBACK_REPEAT_ERROR);
        }
        orderDao.actionDrawbackStatus(orderNum, OrderController.DrawbackStatus.WAIT_APPROVE);
        DrawBackAmount drawBackAmount = orderDao.getDrawbackAmount(orderNum);
        drawBackAmount.calcDisplayTotal();
        orderDao.creteDrawbackOrder(userId, simpleOrder.getId(), drawbackRequest, drawBackAmount.getDisplayTotalAmount(), drawBackAmount.getDisplayTotalYue() , drawBackAmount.getDisplayTotalYongjin());
        return ResultJson.ok();
    }

    public ResultJson<DrawBackAmount> drawbackOrderAmount(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!(OrderController.BuyerOrderStatus.WAIT_SEND.toString().equalsIgnoreCase(simpleOrder.getStatus())
                || OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString().equalsIgnoreCase(simpleOrder.getStatus())
                || OrderController.BuyerOrderStatus.WAIT_COMMENT.toString().equalsIgnoreCase(simpleOrder.getStatus()))){
            return ResultJson.failure(HttpYzCode.ORDER_STATUS_ERROR);
        }

        DrawBackAmount drawBackAmount = orderDao.getDrawbackAmount(orderNum);
        drawBackAmount.calcDisplayTotal();
        return ResultJson.ok(drawBackAmount);
    }

    public ResultJson<DrawbackOrderDetailInfo> getDrawbackOrderDetail(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        DrawbackOrderDetailInfo di = orderDao.getDrawbackOrderDetail(simpleOrder.getId());
        return ResultJson.ok(di);
    }

    public ResultJson<List<OrderListItemInfo>> getOrderList(int userId, OrderController.BuyerOrderStatus orderType, int pageSize, int pageNum) {
        List<OrderListItemInfo> os = orderDao.getBuyerOrderList(userId, orderType, pageSize, pageNum);
        fillOrderItemImg(os);
        return ResultJson.ok(os);
    }

    private void fillOrderItemImg(List<OrderListItemInfo> os) {
        List<Integer> orderids = os.stream().map(o -> o.getId()).collect(Collectors.toList());

        List<OrderListItemInfo.OrderItemsForListPage> orderItemsForListPage = orderDao.getOrderItemsForListPage(orderids);
        Map<Integer, List<OrderListItemInfo.OrderItemsForListPage>> itemImgsMap = orderItemsForListPage.stream().collect(Collectors.groupingBy(OrderListItemInfo.OrderItemsForListPage::getOrderId));
        os.stream().forEach(o -> {
            List<OrderListItemInfo.OrderItemsForListPage> items = itemImgsMap.get(o.getId());
            o.setProductImges(items.stream().map(i -> i.getImage()).collect(Collectors.toList()));
            o.setTotalItemCount(items.size());
        });
    }

    public ResultJson<OrderDetailInfo> getOrderDetail(int userId, String orderNum, boolean admin) {
        OrderDetailInfo orderDetailInfo =  orderDao.getOrderDetail(orderNum);
        if(orderDetailInfo == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!admin && !orderDetailInfo.getUserId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        List<OrderDetailItemInfo> items = orderDao.getOrderDetailItem(orderDetailInfo.getId());
        orderDetailInfo.setItems(items);
        String jianhou = userService.getUserName(orderDetailInfo.getJianhuoyuanId());
        orderDetailInfo.setJianhuoyuanName(jianhou);
        return ResultJson.ok(orderDetailInfo);
    }

    @Transactional
    public void commentOrder(int userId, String orderNum, List<OrderCommentsRequest> orderCommentsRequest) {
        List<Integer> ids = orderCommentsRequest.stream().map(r -> r.getOrderItemId()).collect(Collectors.toList());
        List<SimpleOrderItem> oids = orderDao.getSimpleOrderItem(ids);
        Map<Integer, List<SimpleOrderItem>> simpleOrderItemMap = oids.stream().collect(Collectors.groupingBy(SimpleOrderItem::getOrderItemId));
        orderCommentsRequest.stream().forEach(oc -> {
            SimpleOrderItem item = simpleOrderItemMap.get(oc.getOrderItemId()).get(0);
            oc.setProductName(item.getProductName());
            oc.setProductSize(item.getProductSize());
            oc.setProductProfileImg(item.getProductProfileImg());
        });
        productService.createComment(userId, orderCommentsRequest);
        productService.addCommentCount(orderCommentsRequest.stream().map(o -> o.getProductId()).collect(Collectors.toList()));
        orderDao.finishOrder(orderNum);
    }

    public List<OrderListItemInfo> getAdminOrderList(OrderAdminController.AdminOrderStatus orderType, int pageSize, int pageNum) {
        List<OrderListItemInfo> os = orderDao.getAdminFahuoOrderList(orderType, pageSize, pageNum);
        fillOrderItemImg(os);
        return os;
    }

    public ResultJson adminActionOrder(int userId, String orderNum, OrderAdminController.AdminActionOrderType actionType, String attach) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        switch (actionType){
            case SEND:
                orderDao.fahuo(userId, orderNum);
                break;
            case DRAWBACK_APPROVE_PASS:
                orderDao.adminApproveDrawback(userId, simpleOrder.getId(), orderNum, OrderController.DrawbackStatus.APPROVE_PASS, attach);
                break;
            case DRAWBACK_APPROVE_FAIL:
                orderDao.adminApproveDrawback(userId, simpleOrder.getId(), orderNum, OrderController.DrawbackStatus.APPROVE_REJECT, attach);
                break;
        }
        return ResultJson.ok();
    }

    public ResultJson updateChajiaOrder(String orderNum, ChaJiaOrderItemRequest chajia) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if( OrderAdminController.JianHYOrderStatus.NOT_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
        || simpleOrder.getJianhuoyuanId() == null ){
            return ResultJson.failure(HttpYzCode.ORDER_NO_JIANHUO);
        }

        SimpleOrderItem simpleOrderItem = orderDao.getSimpleOrderItem(chajia.getId());
        if(simpleOrderItem == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(simpleOrderItem.isProductSanZhuang() && (chajia.getChajiaTotalPrice() == null || chajia.getChajiaTotalPrice().compareTo(BigDecimal.ZERO) <= 0 || StringUtils.isBlank(chajia.getChajiaTotalWeight())) ){
            return ResultJson.failure(HttpYzCode.BAD_REQUEST);
        }
        if(simpleOrderItem.isProductSanZhuang()){
            orderDao.updateChajiaOrder(simpleOrder.getId(), chajia);
        }
       orderDao.finishJianhuoItem(simpleOrder.getId(), chajia.getId());
        return ResultJson.ok();
    }

    public ResultJson startJianhuo(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!OrderAdminController.JianHYOrderStatus.NOT_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                && simpleOrder.getJianhuoyuanId() != null ){
            return ResultJson.failure(HttpYzCode.ORDER_JIANHUO_REPEAT);
        }
        orderDao.startJianhuo(userId, simpleOrder.getId());
        return ResultJson.ok();
    }

    public ResultJson finishJianhuo(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!OrderAdminController.JianHYOrderStatus.ING_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                || !simpleOrder.getJianhuoyuanId().equals(userId) ){
            return ResultJson.failure(HttpYzCode.ORDER_JIANHUO_REPEAT);
        }
        orderDao.finishJianhuo(userId, simpleOrder.getId());
        return ResultJson.ok();
    }

    public ResultJson finishJianhuoItem(int userId, String orderNum, Integer orderItemId) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if( OrderAdminController.JianHYOrderStatus.NOT_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                || simpleOrder.getJianhuoyuanId() == null ){
            return ResultJson.failure(HttpYzCode.ORDER_NO_JIANHUO);
        }
        if(!simpleOrder.getJianhuoyuanId().equals(userId)){
            return ResultJson.failure(HttpYzCode.ORDER_JIANHUO_NOT_CURRENT_ORDER);
        }
        orderDao.finishJianhuoItem(simpleOrder.getId(), orderItemId);
        return ResultJson.ok();
    }

    public List<OrderListItemInfo> getDrawbackApproveList(OrderController.DrawbackStatus orderType, int pageSize, int pageNum) {
        List<OrderListItemInfo> drawbackApproveList = orderDao.getDrawbackApproveList(orderType, pageSize, pageNum);
        fillOrderItemImg(drawbackApproveList);
        return drawbackApproveList;
    }

    public List<OrderListItemInfo> getOrderListForJianHuoyuan(OrderAdminController.JianHYOrderStatus orderType, int pageSize, int pageNum) {
        List<OrderListItemInfo> orderListForJianHuoyuan = orderDao.getOrderListForJianHuoyuan(orderType, pageSize, pageNum);
        fillOrderItemImg(orderListForJianHuoyuan);
        return orderListForJianHuoyuan;
    }

    public ResultJson cancelDrawback(int uid, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!OrderController.DrawbackStatus.WAIT_APPROVE.toString().equals(simpleOrder.getDrawbackStatus())){
            return ResultJson.failure(HttpYzCode.ORDER_STATUS_ERROR);
        }
        orderDao.cancelDrawback(orderNum, simpleOrder.getId());
        return ResultJson.ok();
    }

    //cash_fee=1, out_trade_no=P202001292254360001

    /**
     * 确认收款，修改订单状态
     * @param map
     * @return
     */
    @Transactional(readOnly = false, rollbackFor = {Exception.class})
    public int surePayment(Map<String, Object> map) {
        String orderNum = map.get("out_trade_no").toString();
        if(!StringUtils.isNoneBlank(orderNum)){
            return -1;
        }
        if(!map.containsKey("cash_fee")){
            return -1;
        }
        int money = Integer.valueOf(map.get("cash_fee").toString());
        if(money <= 0 ){
            return -1;
        }
        BigDecimal payAmount = BigDecimal.valueOf(money).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.UP);
        String simpon = orderNum.replaceAll("CJ", "");
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(simpon);
        if(simpleOrder == null){
            return -1;
        }
        if(!(simpleOrder.getStatus().equals(OrderController.BuyerOrderStatus.WAIT_PAY) ||
                simpleOrder.getChajiaStatus().equals(OrderAdminController.ChaJiaOrderStatus.WAIT_PAY))){
            return -1;
        }
        orderDao.surePayment(simpleOrder.getId(), payAmount, orderNum.contains("CJ"));
    }
}