package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import com.sm.dao.dao.AdminDao;
import com.sm.dao.dao.OrderDao;
import com.sm.dao.dao.UserAmountLogDao;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.ResultJson;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.order.*;
import com.sm.message.product.ProductListItem;
import com.sm.message.profile.UserAmountInfo;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
    private PaymentService paymentService;

    @Autowired
    private UserAmountLogDao userAmountLogDao;

    public boolean hasWaitPayChajiaOrder(int userID){
        return orderDao.hasWaitPayChajiaOrder(userID);
    }
    /**
     *   5. 删除购物车
     * @param userID
     * @param order
     * @return
     */
    @Transactional
    public ResultJson<String> createOrder(int userID, CreateOrderRequest order) {
        //0. 校验有误差价订单没有支付
        if(orderDao.hasWaitPayChajiaOrder(userID)){
            return ResultJson.failure(HttpYzCode.ORDER_CHAJIA_WAIT_PAY);
        }
        //1. 地址校验
        AddressDetailInfo addressDetail = addressService.getAddressDetail(userID, order.getAddressId());
        if(addressDetail == null){
            return ResultJson.failure(HttpYzCode.ADDRESS_NOT_EXISTS);
        }
        //2. 校验 佣金，和余额
        UserAmountInfo userAmountInfo = userService.getAmount(userID);
        if(order.getUseYongjin() != null && (userAmountInfo == null || userAmountInfo.getYongjin() == null  ||
                userAmountInfo.getYongjin().compareTo(order.getUseYongjin()) < 0  )){
            return ResultJson.failure(HttpYzCode.YONGJIN_YUE_NOT_ENOUGH);
        }
        if(order.getUseYue() != null && (userAmountInfo == null || userAmountInfo.getYue() == null ||
                userAmountInfo.getYue().compareTo(order.getUseYue()) < 0)){
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
        BigDecimal subtract = createOrderInfo.getTotalPrice()
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
        BigDecimal yongjinbase = ServiceUtil.calcCartTotalPriceWithoutZhuanqu(cartItems);
        createOrderInfo.setYongjinBasePrice(yongjinbase == null ? BigDecimal.ZERO: yongjinbase);
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
        //记录使用积分佣金日志
        createAmountLog(createOrderInfo);
        //产品销量加加
        productService.addSalesCount(collect.stream().map(o -> o.getProductId()).collect(Collectors.toList()));
        // 库存减少
        HashMap<Integer, Integer> pid2cnt = new HashMap<>();
        collect.stream().forEach(p -> {
            pid2cnt.put(p.getProductId(), p.getProductCnt());
        });
        productService.subStock(pid2cnt);

        //删除购物车
        shoppingCartService.deleteCartItem(userID, order.getCartIds());

        return ResultJson.ok(createOrderInfo.getOrderNum());
    }

    private void createAmountLog(CreateOrderInfo createOrderInfo) {
        if(createOrderInfo.getUseYongjin() != null && createOrderInfo.getUseYongjin().compareTo(BigDecimal.ZERO) > 0){
            userAmountLogDao.useYongjin(createOrderInfo);

        }
        if(createOrderInfo.getUseYue() != null && createOrderInfo.getUseYue().compareTo(BigDecimal.ZERO) > 0){
            userAmountLogDao.useYue(createOrderInfo);
        }
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
            if(StringUtils.isNoneBlank(simpleOrder.getYongjincode())){
                //佣金计算
                BigDecimal yongJinPercent = adminDao.getYongJinPercent();
                BigDecimal total = simpleOrder.getYongjinBasePrice();
                if (yongJinPercent.compareTo(BigDecimal.ZERO) > 0 && yongJinPercent.compareTo(BigDecimal.ONE) < 0 && total !=null && total.compareTo(BigDecimal.ZERO)> 0){
                    adminDao.updateYongjinAndAddLog(simpleOrder.getYongjincode(), total.multiply(yongJinPercent).setScale(2, RoundingMode.DOWN), simpleOrder, yongJinPercent);
                    logger.info("Update yongjin for order {}/{}, order total {}, yongjin = [{} * {}], ", simpleOrder.getId(), simpleOrder.getOrderNum(), simpleOrder.getNeedPayMoney(), total, yongJinPercent);
                }
            }
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
        orderDao.creteDrawbackOrder(userId, simpleOrder.getId(), drawbackRequest, drawBackAmount.getDisplayTotalAmount(), drawBackAmount.getDisplayTotalYue() , drawBackAmount.getDisplayTotalYongjin(), drawBackAmount);
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

    @Transactional
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
                //发起退款
                doDrawback(simpleOrder);
                break;
            case DRAWBACK_APPROVE_FAIL:
                orderDao.adminApproveDrawback(userId, simpleOrder.getId(), orderNum, OrderController.DrawbackStatus.APPROVE_REJECT, attach);
                break;
        }
        return ResultJson.ok();
    }

    private void doDrawback(SimpleOrder simpleOrder){

        SortedMap<String, String> data = new TreeMap<>();
        //这个不能使中文，否则出错

        if(simpleOrder.getHadPayMoney() != null && simpleOrder.getHadPayMoney().compareTo(BigDecimal.ZERO) > 0){
            data.put("out_refund_no", simpleOrder.getOrderNum()+"DW");
            data.put("out_trade_no", simpleOrder.getOrderNum());
            int found = simpleOrder.getHadPayMoney().multiply(BigDecimal.valueOf(100)).intValue();
            data.put("total_fee", found + "");
            data.put("refund_fee", found + "");
            logger.info("Start dwawback for order {}, refound amount = {}  ", simpleOrder.getOrderNum(), found);
            String result = paymentService.refund(data);
            if (result.equals("\"退款申请成功\"")) {
                logger.info("Dwawback SUCCESS for order {}, refound amount = {}  ", simpleOrder.getOrderNum(), found);
                orderDao.fillDrawbackNum(simpleOrder.getOrderNum()+"DW", simpleOrder.getId());
            }else{
                logger.error("Dwawback ERROR for order {}, refound amount = {}  ", simpleOrder.getOrderNum(), found);
            }
        }
        if (simpleOrder.getChajiaHadPayMoney() != null && simpleOrder.getChajiaHadPayMoney().compareTo(BigDecimal.ZERO) > 0){
            data.put("out_refund_no", simpleOrder.getOrderNum()+"CJDW");
            data.put("out_trade_no", simpleOrder.getOrderNum()+"CJ");
            int found = simpleOrder.getChajiaHadPayMoney().multiply(BigDecimal.valueOf(100)).intValue();
            data.put("total_fee", found + "");
            data.put("refund_fee", found + "");
            logger.info("Start dwawback for order {}, refound amount = {}  ", simpleOrder.getOrderNum() + "CJ", found);
            String result = paymentService.refund(data);
            if (result.equals("\"退款申请成功\"")) {
                orderDao.fillDrawbackNum(simpleOrder.getOrderNum()+"CJDW", simpleOrder.getId());
                logger.info("Dwawback SUCCESS for order {}, refound amount = {}  ", simpleOrder.getOrderNum()+ "CJ", found);
            }else{
                logger.error("Dwawback ERROR for order {}, refound amount = {}  ", simpleOrder.getOrderNum()+ "CJ", found);
            }
        }
        if (simpleOrder.getUseYongjin()!=null && simpleOrder.getUseYongjin().compareTo(BigDecimal.ZERO) > 0){
            userAmountLogDao.drawbackYongjin(simpleOrder);
        }
        if (simpleOrder.getUseYue() != null && simpleOrder.getUseYue().compareTo(BigDecimal.ZERO) > 0){
            userAmountLogDao.drawbackYue(simpleOrder);
        }




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

    /**
     * 1. 修改jianhuo_status 为已经完成拣货。
     * 2. 如果有差价商品的话，计算差价商品总共需要多退少补的金额。
     * @param userId
     * @param orderNum
     * @return
     */
    @Transactional
    public ResultJson finishJianhuo(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
        }
        if(!OrderAdminController.JianHYOrderStatus.ING_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                || !simpleOrder.getJianhuoyuanId().equals(userId) ){
            return ResultJson.failure(HttpYzCode.ORDER_JIANHUO_REPEAT);
        }
        List<OrderDetailItemInfo> items = orderDao.getOrderDetailItem(simpleOrder.getId());
        List<OrderDetailItemInfo> sanzhuangitems = items.stream().filter(odi -> odi.isProductSanzhuang()).collect(Collectors.toList());
        if(!sanzhuangitems.isEmpty()){
            //chajia_status,chajia_price,chajia_need_pay_money
            BigDecimal chajiaprice = sanzhuangitems.stream().map(oi -> {
                BigDecimal chajiatotal = oi.getChajiaTotalPrice();
                BigDecimal total = oi.getProductTotalPrice();
                if(chajiatotal != null && total != null){
                    return chajiatotal.subtract(total).setScale(2, RoundingMode.UP);
                }
                return BigDecimal.ZERO;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);

            orderDao.finishJianhuoWithChajia(userId, simpleOrder.getId(), chajiaprice);
        }else{
            orderDao.finishJianhuo(userId, simpleOrder.getId());
        }



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
        if(!(simpleOrder.getStatus().equals(OrderController.BuyerOrderStatus.WAIT_PAY.toString()) ||
                simpleOrder.getChajiaStatus().equals(OrderAdminController.ChaJiaOrderStatus.WAIT_PAY.toString()))){
            return -1;
        }
        orderDao.surePayment(simpleOrder.getId(), payAmount, orderNum.contains("CJ"));
        return 1;
    }

    public int sureDrawbackPayment(BigDecimal refAmnt, String refundNum, String orderNum) {
        return orderDao.sureDrawbackPayment(refAmnt, refundNum, orderNum);
    }

    public ResultJson adminUpdateChajiaOrder(String orderNum, ChaJiaOrderItemRequest chajia) {
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

        List<OrderDetailItemInfo> items = orderDao.getOrderDetailItem(simpleOrder.getId());
        List<OrderDetailItemInfo> sanzhuangitems = items.stream().filter(odi -> odi.isProductSanzhuang()).collect(Collectors.toList());
        if(!sanzhuangitems.isEmpty()) {
            //chajia_status,chajia_price,chajia_need_pay_money
            BigDecimal chajiaprice = sanzhuangitems.stream().map(oi -> {
                BigDecimal chajiatotal = oi.getChajiaTotalPrice();
                BigDecimal total = oi.getProductTotalPrice();
                if (chajiatotal != null && total != null) {
                    return chajiatotal.subtract(total).setScale(2, RoundingMode.UP);
                }
                return BigDecimal.ZERO;
            }).reduce(BigDecimal.ZERO, BigDecimal::add);
            orderDao.adminfinishCalcChajia(simpleOrder.getId(), chajiaprice);
        }
        return ResultJson.ok();
    }
}