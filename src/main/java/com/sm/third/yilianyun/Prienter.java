package com.sm.third.yilianyun;

import com.sm.third.message.OrderItem;
import com.sm.third.message.OrderPrintBean;
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
	@Autowired
	private LYYService lyyService;
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
		sb.append("<center>悠哉商城\r\n</center>");

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
				sb.append("<td>"+item.getName()+"</td>");
				sb.append("<td>"+item.getSize() +"x" + item.getCount()+"</td>");
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

//		sb.append("订单总价：￥"+orderPrintBean.getTotalPrice() != null ? orderPrintBean.getTotalPrice().setScale(2, RoundingMode.UP).toPlainString() : "" +"\r\n");
//		if(orderPrintBean.isChajia()){
//			sb.append("差价金额：￥"+orderPrintBean.getChajiaNeedPayMoney() != null ? orderPrintBean.getChajiaNeedPayMoney().setScale(2, RoundingMode.UP).toPlainString():""+"\r\n");
//		}
//
//		sb.append("客户支付订单：￥"+orderPrintBean.getHadPayMoney()!= null ? orderPrintBean.getHadPayMoney().setScale(2, RoundingMode.UP).toPlainString():""+"\r\n");
//		if(orderPrintBean.isChajia()) {
//			sb.append("客户支付差价：￥" + orderPrintBean.getChajiaHadPayMoney()!= null ?orderPrintBean.getChajiaHadPayMoney().setScale(2, RoundingMode.UP).toPlainString():""  + "\r\n");
//		}
		sb.append("<center>谢谢惠顾，欢迎下次光临！</center>");
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try{
					lyyService.print(lyyService.mochineCode, sb.toString(), orderPrintBean.getOrderNum());
				}catch (Exception e){
					log.error("Print error", e);
				}

			}
		});
	}
}