package com.sm.service;

import com.sm.controller.OrderAdminController;
import com.sm.controller.TuangouController;
import com.sm.dao.dao.OrderDao;
import com.sm.dao.dao.TuangouDao;
import com.sm.dao.domain.Tuangou;
import com.sm.message.address.AddressDetailInfo;
import com.sm.message.order.DrawbackRequest;
import com.sm.message.order.OrderDetailInfo;
import com.sm.message.order.SimpleOrder;
import com.sm.message.tuangou.TuangouListItemInfo;
import com.sm.message.tuangou.TuangouOrderInfo;
import com.sm.message.tuangou.TuangouSelfStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.sm.controller.OrderAdminController.AdminActionOrderType.DRAWBACK_APPROVE_PASS;
import static com.sm.message.order.DrawbackRequest.Reason.TUANGOU_FAILD_DRAWBACK;
import static com.sm.message.order.DrawbackRequest.Reason.TUANGOU_SUCCESS_DRAWBACK;

@Component
public class TuangouService {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TuangouDao tuangouDao;

    @Autowired
    private AddressService addressService;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDao orderDao;

    public Tuangou createAndGet(Integer addressManagerId) {
        Integer tuangouId = tuangouDao.createTuangou(addressManagerId);
        return tuangouDao.selectById(tuangouId);
    }


    //单线程解决并发问题，如果多线程要加锁
    public void processTuangou(OrderDetailInfo orderDetailInfo) {
        singleThreadExecutor.submit(() -> {
            this.doProcessTuangou(orderDetailInfo);
        });
    }

    /**
     * 1. 非并发
     * 1.1 小区没有
     * 1.2 小区有
     * 1.2.1 人数达标
     * 1.2.2 人数不达标
     * 1.2.3 人数超标
     * 2. 并发
     * 回调：
     * 团购表获取或者新增团购ID
     * 1. 查询
     * 1.1 如果表中没有进行中该小区的团购
     * 新增 + 查询
     * <p>
     * 1.2 上一步进行中的该小区的团购
     * 1.2.1 如果 threashold <= cnt 修改状态。 新增+查询
     * <p>
     * <p>
     * 2. 更新order表，写入团购id
     * 3. 更新团购表 update tuangou set cnt = cnt + 1 where cnt = cnt
     */

    @Transactional
    public void doProcessTuangou(OrderDetailInfo orderDetailInfo) {
        log.info("start attach  tuangou addressID= {}， orderID= {}", orderDetailInfo.getAddressId(), orderDetailInfo.getId());
        //1. 查询进行中的团购
        AddressDetailInfo addressDetail = addressService.getAddressDetail(orderDetailInfo.getUserId(), orderDetailInfo.getAddressId());
        Integer addressManagerId = addressDetail.getReceiveAddressManagerInfo().getId();
        Tuangou tuangou = tuangouDao.selectIngByAdminAddressId(addressManagerId);
        if (tuangou == null) {//1.1 如果表中没有进行中该小区的团购
            tuangou = this.createAndGet(addressManagerId);
            log.info("start attach  tuangou addressID= {}， orderID= {}, tuangou is null, new tuangou id = {}", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), tuangou.getId());
        }

        if (tuangou.getThreshold() <= tuangou.getOrderCount()) {//1.2 如果团购到阈值了
            int oldId = tuangou.getId();
            int rst = tuangouDao.finishTuangou(tuangou);
            doFinishSingleTuangou(tuangou);
            if (rst != 1) {
                log.error("start attach  tuangou addressID= {}， orderID= {}, tuangou={} finish, update error", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), tuangou.getId());
            }
            tuangou = this.createAndGet(addressManagerId);
            log.info("start attach  tuangou addressID= {}， orderID= {}, tuangou={} threshold < order cnt, new tuangou id = {}", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), oldId, tuangou.getId());
        }
        //2. 更新order表，写入团购id
        orderDao.updateTuangou(tuangou.getId(), orderDetailInfo.getId());
        //3. 更新团购表 update tuangou set cnt = cnt + 1 where cnt = cnt

        if (tuangou.getThreshold() <= tuangou.getOrderCount() + 1) {
            log.info("start attach  tuangou addressID= {}， orderID= {}, tuangou={} finish", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), tuangou.getId());
            int rst = tuangouDao.finishTuangou(tuangou);
            doFinishSingleTuangou(tuangou);
            if (rst != 1) {
                log.error("start attach  tuangou addressID= {}， orderID= {}, tuangou={} finish, update error", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), tuangou.getId());
            }
        } else {
            log.info("start attach  tuangou addressID= {}， orderID= {}, tuangou={} cnt + 1", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), tuangou.getId());
            int rst = tuangouDao.incrTuangou(tuangou);
            if (rst != 1) {
                log.error("start attach  tuangou addressID= {}， orderID= {}, tuangou={} cnt + 1, update error", orderDetailInfo.getAddressId(), orderDetailInfo.getId(), tuangou.getId());
            }
        }
    }

    //打印其他团购单子
    public void doFinishSingleTuangou(Tuangou tuangou) {

        List<OrderDetailInfo> orders = orderDao.getOrdersByTuangouId(tuangou.getId());

        orders.stream().forEach(o -> {
            if(o.getTuangouId() > 0 && OrderService.TUANGOU_MOD.TUANGOU.name().equals(o.getTuangouMod())){
                orderDao.updateJianhuoStatus(o.getId(), OrderAdminController.JianHYOrderStatus.NOT_JIANHUO);
                orderService.printOrder(o);

            }
        });

    }

    public void doCancelAllIngTuangou() {
        //Load all团购团购
        List<Tuangou> tuangous = tuangouDao.loadAllIngTuangou();
        //遍历，然后单个 doFinishSingleTuangou
        tuangous.stream().forEach(tuangou ->{
            List<OrderDetailInfo> orderDetailInfos = orderDao.queryTuangouModOrderByTuangouId(tuangou.getId());
            orderDetailInfos.stream().forEach(o -> {
                try{
                    //拼装退款的单子请求
                    DrawbackRequest drawbackRequest = new DrawbackRequest();
                    drawbackRequest.setUserId(o.getUserId());
                    drawbackRequest.setType("退款");
                    drawbackRequest.setDetail("成团失败，系统自动发起退款！");
                    drawbackRequest.setOrderNum(o.getOrderNum());
                    drawbackRequest.setReason(TUANGOU_FAILD_DRAWBACK.name());
                    orderService.drawbackOrder(o.getUserId(), drawbackRequest);

                    //审批通过
                    orderService.adminActionOrder(137, o.getOrderNum(), null, DRAWBACK_APPROVE_PASS, "未成团单子，系统自动审批！");
                    log.info("自动成团退款成功，orderId = {}", o.getId());
                }catch (Exception e){
                    log.error("成团失败，退款失败！orderId = {}", o.getId(), e);
                }
            });
            tuangouDao.calcelTuangou(tuangou.getId());
        });


    }

    public void drawbackTuangouAmount(SimpleOrder simpleOrder) {
        //只有SINGLE 的才能退款
        if(simpleOrder.getTuangouId() != null && simpleOrder.getTuangouId() > 0
            && OrderService.TUANGOU_MOD.SINGLE.name().equals(simpleOrder.getTuangouMod()) &&
                simpleOrder.getTuangouDrawbackAmount().compareTo(BigDecimal.ZERO) > 0){
            DrawbackRequest drawbackRequest = new DrawbackRequest();
            drawbackRequest.setUserId(simpleOrder.getUserId());
            drawbackRequest.setOrderItemId(Integer.MAX_VALUE);
            drawbackRequest.setType("退款");
            drawbackRequest.setDetail("团购成功，退还差价。订单总金额，￥"+simpleOrder.getTotalPrice() +"，需要退款的团购金额￥"+simpleOrder.getTuangouDrawbackAmount());
            drawbackRequest.setOrderNum(simpleOrder.getOrderNum());
            drawbackRequest.setReason(TUANGOU_SUCCESS_DRAWBACK.getDesc());
            log.info("团购成功，退还差价, {}", simpleOrder.getId());
            orderDao.saveDrawback(simpleOrder.getId(), String.valueOf(Integer.MAX_VALUE), drawbackRequest.getType(),
                    drawbackRequest.getReason(), drawbackRequest.getDetail(), simpleOrder.getTuangouDrawbackAmount(), BigDecimal.ZERO,BigDecimal.ZERO,
                    "", simpleOrder.getTuangouDrawbackAmount(),BigDecimal.ZERO,false, BigDecimal.ZERO, true);

            //审批通过
            orderService.adminActionOrder(137, simpleOrder.getOrderNum(), Integer.MAX_VALUE, DRAWBACK_APPROVE_PASS, "未成团单子，系统自动审批！");
        }
    }

    public List<TuangouListItemInfo> getTuangouList(Integer userId, TuangouController.QueryType queryType, TuangouController.StatusType statusType, int pageSize, int pageNum, int addressId) {
        List<Integer> tuangouIDs = new ArrayList<>();
        TuangouDao.TUANGOU_STATUS status = TuangouDao.TUANGOU_STATUS.getByQueryType(statusType);
        if(TuangouController.QueryType.SELF.equals(queryType)){
            //查出自己参与的团购单子，订单支付成功，且是 tuangou_mod是 single或者tuangou的单子。
            tuangouIDs.addAll(tuangouDao.filterByStatusAndId(orderDao.queryTuangouId(userId), status));
        }else{//小区
            tuangouIDs.addAll(tuangouDao.getTuangouIdsPagedByStatusAndAddressId(status, pageSize, pageNum, addressId));
        }
        if(CollectionUtils.isEmpty(tuangouIDs)){
            return new ArrayList<>();
        }

        Map<Integer, Tuangou> tuangouId2Detail = tuangouDao.getTuangouDetailsByIds(tuangouIDs).stream().collect(Collectors.toMap(tu -> tu.getId(), tu -> tu, (o1, o2) -> o1));

        List<TuangouOrderInfo> tuangouOrderInfos = orderDao.queryTuangouSimpleOrderByTuangouIds(TuangouController.QueryType.SELF.equals(queryType)? userId : null, tuangouIDs);

        return tuangouOrderInfos.stream().collect(Collectors.groupingBy(TuangouOrderInfo::getTuangouId)).entrySet().stream().map(en -> {
            Tuangou tg = tuangouId2Detail.get(en.getKey());
            return new TuangouListItemInfo(en.getKey(), en.getValue(), tg.getThreshold(), tg.getCreatedTime());
        }).sorted(Comparator.comparing(TuangouListItemInfo::getTuangouId).reversed()).collect(Collectors.toList());
    }


    public int getSelfTuangouCount(int userId) {
        List<Integer> tuangouIDs = new ArrayList<>();
        tuangouIDs.addAll(orderDao.queryTuangouId(userId));
        return tuangouIDs.size();
    }

    public int countIngByXiaoquId(int addressId) {
        return tuangouDao.countIngByXiaoquId(addressId);
    }
}