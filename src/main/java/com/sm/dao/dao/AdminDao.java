package com.sm.dao.dao;

import com.sm.controller.AdminController;
import com.sm.dao.domain.UserAmountLog;
import com.sm.dao.domain.UserAmountLogType;
import com.sm.message.admin.AdminCntInfo;
import com.sm.message.admin.JinXiaoCunInfo;
import com.sm.message.admin.YzStatisticsInfo;
import com.sm.message.admin.YzStatisticsInfoItem;
import com.sm.message.order.SimpleOrder;
import com.sm.utils.SmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sm.controller.OrderController.BuyerOrderStatus.*;

/**
 * @author wanli.zhou
 * @description
 * @time 2020-01-14 22:33
 */
@Component
public class AdminDao {
    Logger log = LoggerFactory.getLogger(AdminDao.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserAmountLogDao userAmountLogDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public BigDecimal getYongJinPercent(){
        final String sql = String.format("select yongjin_percent from %s limit 1 ", VarProperties.ORDER_YONGJIN_PERCENT);
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class);
        }catch (Exception e){
            return BigDecimal.ZERO;
        }

    }

    @Transactional
    public void updateYongjinAndAddLog(String userName, String yongjinCode, BigDecimal total, SimpleOrder order, BigDecimal yongjinpercent) {
        final String userIDSql = String.format("select id from %s where yongjin_code = ?", VarProperties.USERS);
        Integer userId = null;
        try{
            userId = jdbcTemplate.queryForObject(userIDSql, new Object[]{yongjinCode}, Integer.class);
        }catch (Exception e){
            log.error("Get user by yongjin code " + yongjinCode + " error.", e);
        }
        if(userId == null){
            return;
        }

        final String sql = String.format("update %s set yongjin = yongjin + ? where id =?", VarProperties.USERS);
        jdbcTemplate.update(sql, new Object[]{total, userId});

        //Integer userId, BigDecimal amount, String remark, UserAmountLogType logType , String remarkDetail
        UserAmountLog userAmountLog = new UserAmountLog(userId, total, String.format("好友下单赚取佣金(好友昵称：%s)", userName), UserAmountLogType.YONGJIN, String.format("订单号：%s , 订单金额 ： %s, 佣金比例 %s", order.getOrderNum(), order.getYongjinBasePrice(), yongjinpercent.toPlainString()));
        userAmountLogDao.create(userAmountLog);

    }

    public List<JinXiaoCunInfo> getJinxiaocun(int pageSize, int pageNum) {
        final String sql = String.format("select id,name,profile_img from %s where stock = 0 limit ?, ?", VarProperties.PRODUCTS);
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{startIndex, pageSize}, new JinXiaoCunInfo.JinXiaoCunInfoRowMapper());
    }

    public void updateYongjinPercent(BigDecimal value) {
        final String sql = String.format("update %s set yongjin_percent = ?", VarProperties.ORDER_YONGJIN_PERCENT);
        jdbcTemplate.update(sql, new Object[]{value});
    }

    public YzStatisticsInfo getTodayStatistics() {
        YzStatisticsInfo yzStatisticsInfo = new YzStatisticsInfo();
        //online
        final String sql = String.format("select sum(total_price + chajia_price) as total_price, count(1) as total_cnt, sum(total_cost_price) as total_cost,  sum(total_price + chajia_price) - sum(total_cost_price)  as total_profit from %s where status in(:status) and created_time >= :time  ", VarProperties.ORDER);
        HashMap<String, Object> map = new HashMap();
        map.put("status", Stream.of(new String[]{WAIT_SEND.toString(),WAIT_RECEIVE.toString(),WAIT_COMMENT.toString(),FINISH.toString()}).collect(Collectors.toList()));
        map.put("time",SmUtil.getTodayYMD() + " 0:0:0");
        YzStatisticsInfoItem online = namedParameterJdbcTemplate.query(sql, map, new YzStatisticsInfoItem.YzStatisticsInfoItemRowMapper()).stream().findFirst().orElse(null);
        yzStatisticsInfo.setOnline(online);

        //offline
        final String offlineSql = String.format("select count(1) as total_cnt, sum(total_price) as total_price, sum(total_cost_price) as total_cost, sum(total_price) - sum(total_cost_price) as total_profit from %s where status = 'FINISH' and created_time >= ?  ", VarProperties.SHOUYIN_ORDER);
        YzStatisticsInfoItem offline = jdbcTemplate.query(offlineSql, new Object[]{SmUtil.getTodayYMD() + " 0:0:0"},  new YzStatisticsInfoItem.YzStatisticsInfoItemRowMapper()).stream().findFirst().orElse(null);
        yzStatisticsInfo.setOffline(offline);

        yzStatisticsInfo.initTotal();
        return yzStatisticsInfo;
    }
    public YzStatisticsInfo getLastdayStatistics() {
        String start = SmUtil.getLastTodayYMD() + " 0:0:0";
        String end = SmUtil.getTodayYMD() + " 0:0:0";
        long longTimeFromYMDHMS = SmUtil.getLongTimeFromYMDHMS(SmUtil.getLastTodayYMD() + " 00:00:00");

        YzStatisticsInfo info = new YzStatisticsInfo();
        final String sql = String.format("select sum(total_price + chajia_price) as total_price, count(1) as total_cnt, sum(total_cost_price) as total_cost,  sum(total_price + chajia_price) - sum(total_cost_price)  as total_profit from %s where status in(:status) and created_time >= :time1  and created_time <= :time2", VarProperties.ORDER);
        HashMap<String, Object> map = new HashMap();
        map.put("status", Stream.of(new String[]{WAIT_SEND.toString(),WAIT_RECEIVE.toString(),WAIT_COMMENT.toString(),FINISH.toString()}).collect(Collectors.toList()));
        map.put("time1", start);
        map.put("time2", end);
        YzStatisticsInfoItem online = namedParameterJdbcTemplate.query(sql, map, new YzStatisticsInfoItem.YzStatisticsInfoItemRowMapper()).stream().findFirst().orElse(null);
        online.setDatelong(longTimeFromYMDHMS);
        info.setOnline(online);
        //offline
        final String offlineSql = String.format("select count(1) as total_cnt, sum(total_price) as total_price, sum(total_cost_price) as total_cost, sum(total_price) - sum(total_cost_price) as total_profit from %s where status = 'FINISH' and created_time >= ? and created_time<= ? ", VarProperties.SHOUYIN_ORDER);
        YzStatisticsInfoItem offline = jdbcTemplate.query(offlineSql, new Object[]{start, end},  new YzStatisticsInfoItem.YzStatisticsInfoItemRowMapper()).stream().findFirst().orElse(null);
        offline.setDatelong(longTimeFromYMDHMS);
        info.setOffline(offline);
        return info;
    }

    public void createStatistics(YzStatisticsInfo yzStatisticsInfo){
        YzStatisticsInfoItem online = yzStatisticsInfo.getOnline();
        YzStatisticsInfoItem offline = yzStatisticsInfo.getOffline();
        final String sql = String.format("insert into %s (day,total_price,total_cnt,total_cost,total_profit, type) values(?,?,?,?,?, ?)", VarProperties.STATISTICS);
        if(online != null){
            jdbcTemplate.update(sql, new Object[]{online.getDatelong(), online.getTotalPrice(), online.getTotalCnt(), online.getTotalCost(), online.getTotalProfit(), AdminController.StatisticsType.ONLINE.toString()});
        }
        if(offline != null){
            jdbcTemplate.update(sql, new Object[]{offline.getDatelong(), offline.getTotalPrice(), offline.getTotalCnt(), offline.getTotalCost(), offline.getTotalProfit(), AdminController.StatisticsType.OFFLINE.toString()});
        }
    }
    public List<YzStatisticsInfo> getStatistics(Long start, Long end, int pageSize, int pageNum) {
        if(pageNum <= 0 ){
            pageNum = 1;
        }
        if(pageSize <= 0){
            pageSize = 10;
        }
        int startIndex = (pageNum - 1) * pageSize;
        final String sql = String.format("select  day ,total_price ,total_cnt, total_cost , total_profit, type  from %s where day >= ? and day <= ? order by day desc limit ?,? ", VarProperties.STATISTICS);
        List<YzStatisticsInfoItem> query = jdbcTemplate.query(sql, new Object[]{start, end, startIndex, pageSize}, new YzStatisticsInfoItem.YzStatisticsInfoItemRowMapper());
        return query.stream().collect(Collectors.groupingBy(YzStatisticsInfoItem::getDate))
                    .entrySet().stream().map(en -> {
                        List<YzStatisticsInfoItem> value = en.getValue();
                        YzStatisticsInfoItem online = value.stream().filter(i -> i.getType().equals(AdminController.StatisticsType.ONLINE.toString())).findFirst().orElse(null);
                        YzStatisticsInfoItem offline = value.stream().filter(i -> i.getType().equals(AdminController.StatisticsType.OFFLINE.toString())).findFirst().orElse(null);
                        YzStatisticsInfo info = new YzStatisticsInfo();
                        info.setOffline(offline);
                        info.setOnline(online);
                        info.initTotal();
                        return info;
                    }).sorted((a,b)->
                        -a.getOnline().getDate().compareTo(b.getOnline().getDate())
                ).collect(Collectors.toList())
                ;

    }

    public BigDecimal getYongjinPercent() {
        String sql = String.format("select yongjin_percent from %s ", VarProperties.ORDER_YONGJIN_PERCENT);
        return jdbcTemplate.queryForObject(sql, BigDecimal.class);
    }

    public AdminCntInfo countAdminCnt() {
        AdminCntInfo adminCntInfo = new AdminCntInfo();
        adminCntInfo.setOrderManagerCnt(orderDao.countOrderManagerCnt());
        adminCntInfo.setDrawbackManagerCnt(orderDao.countDrawbackManagerCnt());
        adminCntInfo.setFeedbackManagerCnt(orderDao.countFeedbackManagerCnt());
        return adminCntInfo;
    }
}