package com.sm.controller;

import com.sm.config.UserDetail;
import com.sm.message.ResultJson;
import com.sm.message.order.SimpleOrder;
import com.sm.service.OrderService;
import com.sm.service.PaymentService;
import com.sm.utils.AESUtil;
import com.sm.utils.PayUtil;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@RestController
@Api(tags={"pay"})
@RequestMapping("/api/v1/")
public class PaymentController {
 
	private static Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private PaymentService paymentService;
 	@Autowired
	private OrderService orderService;
	@Value("${sm.wx.key}")
	public  String XCX_KEY;

	/**
	 * <p>统一下单入口</p>
	 *
	 * @throws Exception
	 */
	@PostMapping(path = "payment/toPay/{ordernum}/{payType}")
	@PreAuthorize("hasAuthority('BUYER') ")
	@ApiResponses(value={@ApiResponse(code= 420, message="订单不存在"),
			@ApiResponse(code=421, message="订单状态不对")})
	public ResultJson<Map<String, String>> toPay(@Valid @NotNull @PathVariable("ordernum") String orderNum,
												 @Valid @NotNull @PathVariable("payType") PayType payType) throws Exception {
	   	//获取订单 如果订单号一样会
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		final UserDetail userDetail = (UserDetail) authentication.getPrincipal();
		String openID = userDetail.getOpenId();

		SimpleOrder simpleOrder = orderService.getSimpleOrder(orderNum);
		if(simpleOrder == null || !simpleOrder.getUserId().equals(userDetail.getId())){
			return ResultJson.failure(HttpYzCode.ORDER_NOT_EXISTS);
		}
		if(!(PayType.ORDER.equals(payType) && OrderController.BuyerOrderStatus.WAIT_PAY.toString().equals(simpleOrder.getStatus()))
		&& !(PayType.CHAJIA.equals(payType) && OrderAdminController.ChaJiaOrderStatus.WAIT_PAY.toString().equals(simpleOrder.getChajiaStatus()))){
			return ResultJson.failure(HttpYzCode.ORDER_STATUS_ERROR);
		}

		int amount = simpleOrder.getNeedPayMoney().multiply(BigDecimal.valueOf(100)).intValue();
		if(PayType.CHAJIA.equals(payType)){
			amount = simpleOrder.getChajiaNeedPayMoney().multiply(BigDecimal.valueOf(100)).intValue();
			orderNum = orderNum + "CJ";
		}
		//校验 订单是否存在？ 是否值已经支付？
		logger.info("【小程序支付服务】请求订单编号:[{}, 金额： {}]", orderNum, amount);
		Map<String, String> resMap = paymentService.xcxPayment(orderNum, amount ,openID);
		if("SUCCESS".equals(resMap.get("returnCode")) && "OK".equals(resMap.get("returnMsg"))){
			//统一下单成功
			logger.info("【小程序支付服务】获取统一下单请求成功！");
			return ResultJson.ok(resMap);
		}else{
			logger.info("【小程序支付服务】获取统一下单请求失败！原因:"+resMap.get("returnMsg"));
			return ResultJson.failure(HttpYzCode.SERVER_ERROR, resMap.get("returnMsg"));
		}
 
	}

	String payReturn = "<xml>\n" +
			"\n" +
			"  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
			"  <return_msg><![CDATA[OK]]></return_msg>\n" +
			"</xml>";
    /**
	 * <p>回调Api</p>
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@PostMapping(path = "payment/callback")
	public String xcxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		InputStream inputStream =  request.getInputStream();
		//获取请求输入流
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len=inputStream.read(buffer))!=-1){
			outputStream.write(buffer,0,len);
		}
		outputStream.close();
		inputStream.close();
		Map<String,Object> map = PayUtil.getMapFromXML(new String(outputStream.toByteArray(),"utf-8"));
		logger.info("【小程序支付回调】 回调数据： \n"+map);
		String returnCode = (String) map.get("return_code");
		if ("SUCCESS".equalsIgnoreCase(returnCode)) {
			String returnmsg = (String) map.get("result_code");
			if("SUCCESS".equals(returnmsg)){
				//更新数据
				int result = orderService.surePayment(map);
				if(result > 0){
					logger.info("【订单支付成功】for order {}, amount: {} ",  map.get("out_trade_no") , map.get("cash_fee"));
					response.setContentType("text/xml");
					response.getOutputStream().write(payReturn.getBytes());
					response.getOutputStream().flush();
					response.getOutputStream().close();
					return payReturn;
				}
				logger.error("【订单支付成功,但是更改订单状态失败】for order "+map.get("out_trade_no") +", amount:  " + map.get("cash_fee"));
			}else{
				logger.info("【订单支付失败】111");
				return null;

			}
		}else{
			logger.info("【订单支付失败】error");
			return null;
		}
		return null;
	}

	String refountReturn = "<xml>\n" +
			"  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
			"  <return_msg><![CDATA[OK]]></return_msg>\n" +
			"</xml>";

	@PostMapping(path = "payment/refund/callback")
	public String xcxdrwabackNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		InputStream inputStream =  request.getInputStream();
		//获取请求输入流
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len=inputStream.read(buffer))!=-1){
			outputStream.write(buffer,0,len);
		}
		outputStream.close();
		inputStream.close();
		Map<String,Object> map = PayUtil.getMapFromXML(new String(outputStream.toByteArray(),"utf-8"));
		logger.info("【小程序退款回调】 回调数据： \n"+map);
		String returnCode = (String) map.get("return_code");
		if ("SUCCESS".equalsIgnoreCase(returnCode)) {
			//更新数据
			String reqInfo = map.get("req_info").toString();
			AESUtil.init(XCX_KEY);
			String resultStr = AESUtil.decryptData(reqInfo);
			Map<String, Object> mapFromXML = PayUtil.getMapFromXML(resultStr);
			if("SUCCESS".equals(mapFromXML.get("refund_status").toString())){
				int refount = Integer.valueOf(mapFromXML.get("refund_fee").toString());
				String refundNum = mapFromXML.get("out_refund_no").toString();
				BigDecimal refAmnt = BigDecimal.valueOf(refount).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.UP);
				String orderNum = mapFromXML.get("out_trade_no").toString();
				int result = orderService.sureDrawbackPayment(refAmnt, refundNum, orderNum);
				if(result > 0){
					logger.info("==={}", mapFromXML);
					logger.info("【订单退款成功】for order {}, amount: {} ",  refundNum , refAmnt);

					response.setContentType("text/xml");
					response.getOutputStream().write(refountReturn.getBytes());
					response.getOutputStream().flush();
					response.getOutputStream().close();

					return refountReturn;
				}else{
					logger.error("【订单退款成功,但是更改订单状态失败】for order "+refundNum +", amount:  " + refAmnt);
				}
			}else{

			}


		}else{
			logger.info("【订单支付失败】error");
			return null;
		}
		return null;
	}

	public static enum PayType{
		ORDER,
		CHAJIA
	}
}