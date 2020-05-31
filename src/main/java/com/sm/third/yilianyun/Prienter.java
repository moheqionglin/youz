package com.sm.third.yilianyun;

import com.sm.message.shouyin.ShouYinOrderInfo;
import com.sm.message.shouyin.ShouYinOrderItemInfo;
import com.sm.message.shouyin.ShouYinWorkRecordStatisticsInfo;
import com.sm.third.message.OrderItem;
import com.sm.third.message.OrderPrintBean;
import com.sm.utils.SmUtil;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.test.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小票模板
 * 
 * @author admin
 * 
 */
@Component
public class Prienter{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	Random random = new Random();
	@Autowired
	private LYYService lyyService;
	@Autowired
	private LYYOfflineService lyyOfflineService;
	private ExecutorService executorService = Executors.newFixedThreadPool(1);;
	// 菜品集合--传入一个商品集合
	public static List<Test> testList = new ArrayList<Test>();
	public static double ZMoney=0;
	public static double YMoney=20;
	public static double SMoney=500;

	// 设置小票打印
	public void print(OrderPrintBean orderPrintBean){
		//字符串拼接
		StringBuilder sb=new StringBuilder();
		sb.append("<center>悠哉到家\r\n</center>");

		sb.append("<center>订单内容\r\n</center>");
		sb.append("------------------------------------\r\n");
		sb.append("订单号："+orderPrintBean.getOrderNum()+"\r\n");
		sb.append("下单时间："+orderPrintBean.getOrderTime()+"\r\n");
		sb.append("收货地址："+orderPrintBean.getAddress()+"\r\n");
		sb.append("联系人："+orderPrintBean.getLink()+"\r\n");
		sb.append("备注："+orderPrintBean.getMessage()+"\r\n");
		sb.append("------------------------------------\r\n");
		sb.append("<table>");
			sb.append("<tr>");
				sb.append("<td>");
				sb.append("商品");
				sb.append("</td>");
				sb.append("<td>");
				sb.append("规格");
				sb.append("</td>");
				sb.append("<td>");
				sb.append("价格");
				sb.append("</td>");
//				sb.append("<td>");
//				sb.append("差价信息");
//				sb.append("</td>");
			sb.append("</tr>");
			for (OrderItem item : orderPrintBean.getItems()) {
			sb.append("<tr>");
				sb.append("<td>"+StringUtils.substring(item.getName(), 0, 5)+"</td>");
				sb.append("<td>"+StringUtils.substring(item.getSize(), 0 , 4) +"x" + item.getCount()+"</td>");
				sb.append("<td>"+item.getAmount().setScale(2, RoundingMode.UP).toPlainString()+"</td>");
//				String chajia = "无";
//				if(item.isChajia() && StringUtils.isNoneBlank(item.getChajiaWeight()) && item.getChajiaAmount() != null && item.getChajiaAmount().compareTo(BigDecimal.ZERO) > 0){
//					chajia =  item.getChajiaWeight() + ", " + item.getChajiaAmount().setScale(2, RoundingMode.UP).toPlainString();
//				}
//				sb.append("<td>"+chajia+"</td>");
			sb.append("</tr>");
			}
		sb.append("</table>");
		sb.append("------------------------------------\r\n");

		StringBuffer sbb = new StringBuffer();
		sbb.append(" 商品总数: " + orderPrintBean.getItems().stream().map(item -> item.getCount()).reduce(0, (a,b)-> a + b));
		sbb.append(" 总金额:" +  orderPrintBean.getTotalPrice());
		sbb.append("  主订单支付金额 " + orderPrintBean.getHadPayMoney());

		if(orderPrintBean.getChajiaHadPayMoney() != null && orderPrintBean.getChajiaHadPayMoney().compareTo(BigDecimal.ZERO) != 0){
			sbb.append("  差价总金额:" +  orderPrintBean.getChajiaHadPayMoney());
			sbb.append("  差价支付金额: " + orderPrintBean.getChajiaHadPayMoney());
		}
		sb.append(sbb.toString());
		sb.append("\n------------------------------------\r\n");

//		sb.append("订单总价：￥"+orderPrintBean.getTotalPrice() != null ? orderPrintBean.getTotalPrice().setScale(2, RoundingMode.UP).toPlainString() : "" +"\r\n");
//		if(orderPrintBean.isChajia()){
//			sb.append("差价金额：￥"+orderPrintBean.getChajiaNeedPayMoney() != null ? orderPrintBean.getChajiaNeedPayMoney().setScale(2, RoundingMode.UP).toPlainString():""+"\r\n");
//		}
//
//		sb.append("客户支付订单：￥"+orderPrintBean.getHadPayMoney()!= null ? orderPrintBean.getHadPayMoney().setScale(2, RoundingMode.UP).toPlainString():""+"\r\n");
//		if(orderPrintBean.isChajia()) {
//			sb.append("客户支付差价：￥" + orderPrintBean.getChajiaHadPayMoney()!= null ?orderPrintBean.getChajiaHadPayMoney().setScale(2, RoundingMode.UP).toPlainString():""  + "\r\n");
//		}
		sb.append("<center>地址: 浦东区新行路395号</center>");
		sb.append("<center>电话: 021-68776696</center>");

		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try{
					lyyService.print(lyyService.mochineCode, sb.toString(), System.currentTimeMillis() + " " +random.nextInt(1000));
				}catch (Exception e){
					log.error("Print error", e);
				}

			}
		});
	}


	// 设置小票打印
	public void printShouyinOrder(ShouYinOrderInfo orderInfo){
		//字符串拼接
		StringBuilder sb=new StringBuilder();
		sb.append("<center>悠哉到家\r\n</center>");

		sb.append("------------------------------------\r\n");
		sb.append("订单号："+orderInfo.getOrderNum()+"\r\n");
		sb.append("下单时间："+ SmUtil.parseLongToTMDHMS(System.currentTimeMillis()) +"\r\n");
		sb.append("收银员："+orderInfo.getUserId()+"\r\n");
		sb.append("------------------------------------\r\n");
		sb.append("<table>");
		sb.append("<tr>");
		sb.append("<td>");
		sb.append("商品");
		sb.append("</td>");
		sb.append("<td>");
		sb.append("规格");
		sb.append("</td>");
		sb.append("<td>");
		sb.append("价格");
		sb.append("</td>");
		sb.append("</tr>");
		for (ShouYinOrderItemInfo item : orderInfo.getShouYinOrderItemInfoList()) {
			sb.append("<tr>");
			sb.append("<td>"+StringUtils.substring(item.getProductName(), 0, 5)+"</td>");
			sb.append("<td>"+StringUtils.substring(item.getProductSize(), 0 , 4) +"x" + item.getProductCnt()+"</td>");
			sb.append("<td>"+item.getUnitPrice().setScale(2, RoundingMode.UP).toPlainString()+"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("------------------------------------\r\n");

		StringBuffer sbb = new StringBuffer();
		sbb.append("商品总数 :" + orderInfo.getShouYinOrderItemInfoList().stream().map(item -> item.getProductCnt()).reduce(0, (a,b)-> a + b));
		sbb.append("  总金额 :" + orderInfo.getTotalPrice());

		if( orderInfo.getOnlinePayMoney() != null &&  orderInfo.getOnlinePayMoney().compareTo(BigDecimal.ZERO) != 0){
			sbb.append("  线上支付 :" + orderInfo.getOnlinePayMoney());
		}

		if(orderInfo.getOfflinePayMoney() != null &&  orderInfo.getOfflinePayMoney().compareTo(BigDecimal.ZERO) != 0){
			sbb.append("  现金支付 :" + orderInfo.getOfflinePayMoney());
		}
		if(orderInfo.getZhaoling() != null &&  orderInfo.getZhaoling().compareTo(BigDecimal.ZERO) != 0){
			sbb.append("  找零 :" + orderInfo.getZhaoling());
		}
		sb.append(sbb.toString());
		sb.append("\n------------------------------------\r\n");
		sb.append("<center>地址: 浦东区新行路395号</center>");
		sb.append("<center>电话: 021-68776696</center>");

		executorService.execute(()-> {
			try{
				lyyService.print(lyyOfflineService.mochineCode, sb.toString(), System.currentTimeMillis() + " " +random.nextInt(1000));
			}catch (Exception e){
				log.error("Print error", e);
			}
		});
	}
	// 设置小票打印
	public void printShouyinPerson(ShouYinWorkRecordStatisticsInfo info){
		//字符串拼接
		StringBuilder sb=new StringBuilder();
		sb.append("<center>悠哉到家\r\n</center>");

		sb.append("<center>收银员统计信息\r\n</center>");
		sb.append("------------------------------------\r\n");
		sb.append("姓名："+info.getName()+"\r\n");
		sb.append("工号："+info.getUserId()+"\r\n");
		sb.append("开始收银时间："+info.getStartTimeStr()+"\r\n");
		sb.append("结束收银时间："+info.getEndTimeStr()+"\r\n");
		sb.append("备用金："+info.getBackupAmount()+"\r\n");
		sb.append("总单数："+info.getOrderCnt()+"\r\n");
		sb.append("总收款："+info.getTotalOrderAmount()+"\r\n");
		sb.append("线下收款："+info.getTotalOfflineAmount()+"\r\n");
		sb.append("线上收款："+info.getTotalOnlineAmount()+"\r\n");
		sb.append("------------------------------------\r\n");

		sb.append("<center>祝您工作愉快！</center>");
		executorService.execute(()->{
			try{
				lyyService.print(lyyOfflineService.mochineCode, sb.toString(), System.currentTimeMillis() + " " +random.nextInt(1000));
			}catch (Exception e){
				log.error("Print error", e);
			}
		});
	}
}