package com.sm.controller;

import com.sm.service.PaymentService;
import com.sm.utils.PayUtil;
import com.sun.xml.internal.bind.v2.TODO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Api(tags={"order"})
@RequestMapping("/api/v1/")
public class PaymentController {
 
	private static Logger logger = LoggerFactory.getLogger(PaymentController.class);

	@Autowired
	private PaymentService paymentService;
 
     /**
	 * <p>统一下单入口</p>
	 *
	 * @throws Exception
	 */
	@PostMapping(path = "payment/toPay")
	@PreAuthorize("permitAll()")
	public ResponseEntity<Map<String, String>> toPay() throws Exception {
	   	//获取订单 如果订单号一样会

		String orderNum = "Pu81u98riqw" + new Date().getTime() ;
		//TODO
		String openID = "";
		int amount = 10;
		//校验 订单是否存在？ 是否值已经支付？
		logger.info("【小程序支付服务】请求订单编号:[{}]", orderNum);
		Map<String, String> resMap = paymentService.xcxPayment(orderNum, amount ,openID);
		if("SUCCESS".equals(resMap.get("returnCode")) && "OK".equals(resMap.get("returnMsg"))){
			//统一下单成功
			resMap.remove("returnCode");
			resMap.remove("returnMsg");
			logger.info("【小程序支付服务】支付下单成功！");
			return ResponseEntity.ok(resMap);
		}else{
			logger.info("【小程序支付服务】支付下单失败！原因:"+resMap.get("returnMsg"));
			return ResponseEntity.status(500).body(resMap);
		}
 
	}

    /**
	 * <p>回调Api</p>
	 *
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@GetMapping(path = "payment/callback")
	public ResponseEntity<Map<String,Object>> xcxNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            	int result = paymentService.xcxNotify(map);
            	if(result > 0){
	            	return ResponseEntity.ok(map);
            	}
				logger.info("【订单支付成功】111");
            }else{
				logger.info("【订单支付失败】111");
				return ResponseEntity.status(500).body(null);

            }
        }else{
            logger.info("【订单支付失败】error");
			return ResponseEntity.status(500).body(null);
        }
		return ResponseEntity.status(500).body(null);
	}


	@PostMapping(path = "payment/drawback")
	@PreAuthorize("permitAll()")
	public String drawback() throws Exception {

		SortedMap<String, String> data = new TreeMap<String, String>();
		System.err.println("进入微信退款申请");
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");//可以方便地修改日期格式
		String hehe = dateFormat.format(now);

		//这个不能使中文，否则出错
		String out_refund_no = hehe + "-youzai";

		String yzOrder_num = "Pu81u98riqwyriqwer";
		data.put("out_refund_no", out_refund_no);
		data.put("out_trade_no", yzOrder_num);
		data.put("total_fee", "1");
		data.put("refund_fee", "1");

		String result = paymentService.refund(data);
		//判断是否退款成功
		if (result.equals("\"退款申请成功\"")) {

			return  "已退款";
		}
		return "微信退款失败";

	}
}