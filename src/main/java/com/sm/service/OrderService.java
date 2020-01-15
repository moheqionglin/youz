package com.sm.service;

import com.sm.controller.HttpYzCode;
import com.sm.controller.OrderAdminController;
import com.sm.controller.OrderController;
import com.sm.dao.dao.AdminDao;
import com.sm.dao.dao.OrderDao;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.order.*;
import com.sm.message.product.ProductListItem;
import com.sm.message.profile.UserAmountInfo;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    /**
     *   5. 删除购物车
     * @param userID
     * @param order
     * @return
     */
    @Transactional
    public ResponseEntity<Integer> createOrder(int userID, CreateOrderRequest order) {
        //1. 地址校验
        AddressDetailInfo addressDetail = addressService.getAddressDetail(userID, order.getAddressId());
        if(addressDetail == null){
            return ResponseEntity.status(HttpYzCode.ADDRESS_NOT_EXISTS.getCode()).build();
        }
        //2. 校验 佣金，和余额
        UserAmountInfo userAmountInfo = userService.getAmount(userID);
        if(userAmountInfo == null ||
                userAmountInfo.getYongjin() == null ||  userAmountInfo.getYongjin().compareTo(order.getUseYongjin()) < 0 ||
                userAmountInfo.getYue() == null || userAmountInfo.getYue().compareTo(order.getUseYue()) < 0){
            return ResponseEntity.status(HttpYzCode.YONGJIN_YUE_NOT_ENOUGH.getCode()).build();
        }
        //3. 校验库存
        List<CartItemInfo> cartItems = shoppingCartService.getAllCartItems(userID);
        if(cartItems.stream().filter(c -> c.getProduct().isShowAble()).count() > 0){
            return ResponseEntity.status(HttpYzCode.PRODUCT_XIAJIA.getCode()).build();
        }
        //4. 校验产品下架
        if(cartItems.stream().filter(c -> c.getProduct().getStock() <= c.getProductCnt()).count() > 0){
            return ResponseEntity.status(HttpYzCode.EXCEED_STOCK.getCode()).build();
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
        BigDecimal subtract = createOrderInfo.getTotalCostPrice()
                .subtract(createOrderInfo.getUseYongjin())
                .subtract(createOrderInfo.getUseYue());
        if(subtract.compareTo(BigDecimal.ZERO) <= 0){
            createOrderInfo.setNeedPayMoney(BigDecimal.ZERO);
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

        //佣金计算
        BigDecimal yongJinPercent = adminDao.getYongJinPercent();
        orderDao.createOrderItems(id, collect);
        if(StringUtils.isNoneBlank(createOrderInfo.getYongjinCode()) && yongJinPercent.compareTo(BigDecimal.ZERO) > 0 && yongJinPercent.compareTo(BigDecimal.ONE) < 0){
            BigDecimal total = ServiceUtil.calcCartTotalPriceWithoutZhuanqu(cartItems);
            adminDao.updateYongjin(createOrderInfo.getYongjinCode(), total.multiply(yongJinPercent).setScale(2, RoundingMode.DOWN));
            logger.info("Update yongjin for order {}/{}, order total {}, yongjin = [{} * {}], ", id, createOrderInfo.getOrderNum(), createOrderInfo.getTotalCostPrice(), total, yongJinPercent);
        }
        //产品销量加加
        productService.addSalesCount(collect.stream().map(o -> o.getProductId()).collect(Collectors.toList()));
        return ResponseEntity.ok(id);
    }

    public ResponseEntity actionOrder(int userId, String orderNum, OrderController.ActionOrderType actionType) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if(!(OrderController.BuyerOrderStatus.WAIT_PAY.toString().equalsIgnoreCase(simpleOrder.getStatus()) && OrderController.ActionOrderType.CANCEL_MANUAL.equals(actionType))
        || (OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString().equalsIgnoreCase(simpleOrder.getStatus()) && OrderController.ActionOrderType.RECEIVE.equals(actionType))){
            return ResponseEntity.status(HttpYzCode.ORDER_STATUS_ERROR.getCode()).build();
        }
        OrderController.BuyerOrderStatus bysta = null;
        if(OrderController.ActionOrderType.RECEIVE.equals(actionType)){
            bysta = OrderController.BuyerOrderStatus.WAIT_COMMENT;
        }else {
            bysta = OrderController.BuyerOrderStatus.CANCEL_MANUAL;
        }

        orderDao.actionOrderStatus(orderNum, bysta);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity drawbackOrder(int userId, DrawbackRequest drawbackRequest) {
        String orderNum = drawbackRequest.getOrderNum();
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if(!(OrderController.BuyerOrderStatus.WAIT_SEND.toString().equalsIgnoreCase(simpleOrder.getStatus())
         || OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString().equalsIgnoreCase(simpleOrder.getStatus())
         || OrderController.BuyerOrderStatus.WAIT_COMMENT.toString().equalsIgnoreCase(simpleOrder.getStatus()))){
            return ResponseEntity.status(HttpYzCode.ORDER_STATUS_ERROR.getCode()).build();
        }
        if(!simpleOrder.getDrawbackStatus().equals(OrderController.DrawbackStatus.NONE)){
            return ResponseEntity.status(HttpYzCode.ORDER_DRAWBACK_REPEAT_ERROR.getCode()).build();
        }
        orderDao.actionDrawbackStatus(orderNum, OrderController.DrawbackStatus.WAIT_APPROVE);
        DrawBackAmount drawBackAmount = orderDao.getDrawbackAmount(orderNum);
        drawBackAmount.calcDisplayTotal();
        orderDao.creteDrawbackOrder(userId, simpleOrder.getId(), drawbackRequest, drawBackAmount.getDisplayTotalAmount(), drawBackAmount.getDisplayTotalYue() , drawBackAmount.getDisplayTotalYongjin());
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<DrawBackAmount> drawbackOrderAmount(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if(!(OrderController.BuyerOrderStatus.WAIT_SEND.toString().equalsIgnoreCase(simpleOrder.getStatus())
                || OrderController.BuyerOrderStatus.WAIT_RECEIVE.toString().equalsIgnoreCase(simpleOrder.getStatus())
                || OrderController.BuyerOrderStatus.WAIT_COMMENT.toString().equalsIgnoreCase(simpleOrder.getStatus()))){
            return ResponseEntity.status(HttpYzCode.ORDER_STATUS_ERROR.getCode()).build();
        }

        DrawBackAmount drawBackAmount = orderDao.getDrawbackAmount(orderNum);
        drawBackAmount.calcDisplayTotal();
        return ResponseEntity.ok(drawBackAmount);
    }

    public ResponseEntity<DrawbackOrderDetailInfo> getDrawbackOrderDetail(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || !simpleOrder.getUserId().equals(userId)){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        DrawbackOrderDetailInfo di = orderDao.getDrawbackOrderDetail(simpleOrder.getId());
        return ResponseEntity.ok(di);
    }

    public ResponseEntity<List<OrderListItemInfo>> getOrderList(int userId, OrderController.BuyerOrderStatus orderType, int pageSize, int pageNum) {
        List<OrderListItemInfo> os = orderDao.getBuyerOrderList(userId, orderType, pageSize, pageNum);
        List<Integer> orderids = os.stream().map(o -> o.getId()).collect(Collectors.toList());

        List<OrderListItemInfo.OrderItemsForListPage> orderItemsForListPage = orderDao.getOrderItemsForListPage(orderids);
        Map<Integer, List<OrderListItemInfo.OrderItemsForListPage>> itemImgsMap = orderItemsForListPage.stream().collect(Collectors.groupingBy(OrderListItemInfo.OrderItemsForListPage::getOrderId));
        os.stream().forEach(o -> {
            List<OrderListItemInfo.OrderItemsForListPage> items = itemImgsMap.get(o.getId());
            o.setProductImges(items.stream().map(i -> i.getImage()).collect(Collectors.toList()));
            o.setTotalItemCount(items.size());
        });
        return ResponseEntity.ok(os);
    }

    public ResponseEntity<OrderDetailInfo> getOrderDetail(int userId, String orderNum, boolean admin) {
        OrderDetailInfo orderDetailInfo =  orderDao.getOrderDetail(orderNum);
        if(orderDetailInfo == null){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if(!admin && !orderDetailInfo.getUserId().equals(userId)){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        List<OrderDetailItemInfo> items = orderDao.getOrderDetailItem(orderDetailInfo.getId());
        orderDetailInfo.setItems(items);
        String jianhou = userService.getUserName(orderDetailInfo.getJianhuoyuanId());
        orderDetailInfo.setJianhuoyuanName(jianhou);
        return ResponseEntity.ok(orderDetailInfo);
    }

    public void commentOrder(int userId, String orderNum, List<OrderCommentsRequest> orderCommentsRequest) {
        productService.createComment(userId, orderCommentsRequest);
        productService.addCommentCount(orderCommentsRequest.stream().map(o -> o.getProductId()).collect(Collectors.toList()));
    }

    public List<OrderListItemInfo> getAdminOrderList(OrderAdminController.AdminOrderStatus orderType, int pageSize, int pageNum) {
        List<OrderListItemInfo> os = orderDao.getAdminFahuoOrderList(orderType, pageSize, pageNum);
        List<Integer> orderids = os.stream().map(o -> o.getId()).collect(Collectors.toList());

        List<OrderListItemInfo.OrderItemsForListPage> orderItemsForListPage = orderDao.getOrderItemsForListPage(orderids);
        Map<Integer, List<OrderListItemInfo.OrderItemsForListPage>> itemImgsMap = orderItemsForListPage.stream().collect(Collectors.groupingBy(OrderListItemInfo.OrderItemsForListPage::getOrderId));
        os.stream().forEach(o -> {
            List<OrderListItemInfo.OrderItemsForListPage> items = itemImgsMap.get(o.getId());
            o.setProductImges(items.stream().map(i -> i.getImage()).collect(Collectors.toList()));
            o.setTotalItemCount(items.size());
        });
        return os;
    }

    public void adminActionOrder(int userId, String orderNum, OrderAdminController.AdminActionOrderType actionType, String attach) {
        switch (actionType){
            case SEND:
                orderDao.fahuo(userId, orderNum);
                break;
            case DRAWBACK_APPROVE_PASS:
                orderDao.adminApproveDrawback(userId, orderNum, OrderController.DrawbackStatus.APPROVE_PASS, attach);
                break;
            case DRAWBACK_APPROVE_FAIL:
                orderDao.adminApproveDrawback(userId, orderNum, OrderController.DrawbackStatus.APPROVE_REJECT, attach);
                break;
        }

    }

    public ResponseEntity updateChajiaOrder(String orderNum, ChaJiaOrderItemRequest chajia) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null || chajia.getOrderId().equals(simpleOrder.getId())){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if( OrderAdminController.JianHYOrderStatus.NOT_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
        || simpleOrder.getJianhuoyuanId() == null ){
            return ResponseEntity.status(HttpYzCode.ORDER_NO_JIANHUO.getCode()).build();
        }
        orderDao.updateChajiaOrder(simpleOrder.getId(), chajia);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity startJianhuo(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if(!OrderAdminController.JianHYOrderStatus.NOT_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                && simpleOrder.getJianhuoyuanId() != null ){
            return ResponseEntity.status(HttpYzCode.ORDER_JIANHUO_REPEAT.getCode()).build();
        }
        orderDao.startJianhuo(userId, simpleOrder.getId());
        return ResponseEntity.ok().build();
    }

    public ResponseEntity finishJianhuo(int userId, String orderNum) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if(!OrderAdminController.JianHYOrderStatus.ING_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                || !simpleOrder.getJianhuoyuanId().equals(userId) ){
            return ResponseEntity.status(HttpYzCode.ORDER_JIANHUO_REPEAT.getCode()).build();
        }
        orderDao.finishJianhuo(userId, simpleOrder.getId());
        return ResponseEntity.ok().build();
    }

    public ResponseEntity finishJianhuoItem(int userId, String orderNum, Integer orderItemId) {
        SimpleOrder simpleOrder = orderDao.getSimpleOrder(orderNum);
        if(simpleOrder == null){
            return ResponseEntity.status(HttpYzCode.ORDER_NOT_EXISTS.getCode()).build();
        }
        if( OrderAdminController.JianHYOrderStatus.NOT_JIANHUO.toString().equals(simpleOrder.getJianhuoStatus())
                || simpleOrder.getJianhuoyuanId() == null ){
            return ResponseEntity.status(HttpYzCode.ORDER_NO_JIANHUO.getCode()).build();
        }
        if(!simpleOrder.getJianhuoyuanId().equals(userId)){
            return ResponseEntity.status(HttpYzCode.ORDER_JIANHUO_NOT_CURRENT_ORDER.getCode()).build();
        }
        orderDao.finishJianhuoItem(simpleOrder.getId(), orderItemId);
        return ResponseEntity.ok().build();
    }

    public List<OrderListItemInfo> getDrawbackApproveList(OrderController.DrawbackStatus orderType, int pageSize, int pageNum) {
        return orderDao.getDrawbackApproveList(orderType, pageSize, pageNum);
    }

    public List<OrderListItemInfo> getOrderListForJianHuoyuan(OrderAdminController.JianHYOrderStatus orderType, int pageSize, int pageNum) {
        return orderDao.getOrderListForJianHuoyuan(orderType, pageSize, pageNum);
    }
}