package com.sm.service;

import com.alibaba.fastjson.JSON;
import com.sm.utils.PayUtil;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
@Component
/**
 * 1. 统一下单接口，在微信端 创建一个支付单
 * 2. 根据统一下单的返回的appId,nonceStr,package(prepay_id),signType,timeStamp 创建一个数据签名
 * 3. 小程序调用 wx.requestPayment({timeStamp nonceStr package signType paySign} + sing)
 * 4. 支付成功回调
 */
public class PaymentService {
	private static Logger LOGGER = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private RestTemplate restTemplate;
	@Value("${spring.profiles.active:DEV}")
	private String PROJECT_ENV;

	public static final String TRADE_TYPE_JSAPI = "JSAPI";
	@Value("${sm.wx.appid}")
	public  String XCX_APP_ID;
	@Value("${sm.wx.mid}")
	public  String XCX_MCH_ID;
	@Value("${sm.wx.key}")
	public  String XCX_KEY;

	//微信支付API
	public static final String WX_PAY_UNIFIED_ORDER = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	public Map<String, String> xcxPayment(String orderNum, int money, String openId) throws Exception {
		LOGGER.info("【小程序支付】 统一下单开始, 订单编号={}", orderNum);
		SortedMap<String, String> resultMap = new TreeMap<>();
		//生成支付金额，开发环境处理支付金额数到0.01、0.02、0.03元
		int payAmount = PayUtil.getPayAmountByEnv(PROJECT_ENV, money);
		//添加或更新支付记录(参数跟进自己业务需求添加), 去数据库中再次校验
		int flag = this.addOrUpdatePaymentRecord(orderNum, payAmount);
		if (flag < 0) {
			resultMap.put("returnCode", "FAIL");
			resultMap.put("returnMsg", "此订单已支付！");
			LOGGER.info("【小程序支付】 此订单已支付！");
		} else if (flag == 0) {
			resultMap.put("returnCode", "FAIL");
			resultMap.put("returnMsg", "支付记录生成或更新失败！");
			LOGGER.info("【小程序支付】 支付记录生成或更新失败！");
		} else {
			String productDesc = "悠哉商城支付 商品";
			Map<String, String> resMap = this.xcxUnifieldOrder(orderNum, TRADE_TYPE_JSAPI, payAmount, openId, productDesc);
			if ("SUCCESS".equals(resMap.get("return_code")) && "SUCCESS".equals(resMap.get("result_code"))) {
				resultMap.put("appId", XCX_APP_ID);
				resultMap.put("timeStamp", PayUtil.getCurrentTimeStamp());
				resultMap.put("nonceStr", PayUtil.makeUUID(32));
				resultMap.put("package", "prepay_id=" + resMap.get("prepay_id"));
				resultMap.put("signType", "MD5");
				resultMap.put("sign", PayUtil.createSign(resultMap, XCX_KEY));
				resultMap.put("returnCode", "SUCCESS");
				resultMap.put("returnMsg", "OK");
				LOGGER.info("【小程序支付】统一下单成功，返回参数:" + resultMap);
			} else {
				resultMap.put("returnCode", resMap.get("return_code"));
				resultMap.put("returnMsg", resMap.get("err_code_des"));
				LOGGER.info("【小程序支付】统一下单失败，失败原因:" + resMap.get("return_msg"));
			}
		}
		return resultMap;
	}

	/**
	 * 小程序支付统一下单
	 */
	private Map<String, String> xcxUnifieldOrder(String orderNum, String tradeType, int payAmount, String openid, String productDesc) throws Exception {
		//封装参数
		SortedMap<String, String> paramMap = new TreeMap<String, String>();
		paramMap.put("appid", XCX_APP_ID);
		paramMap.put("mch_id", XCX_MCH_ID);
		paramMap.put("nonce_str", PayUtil.makeUUID(32));
		paramMap.put("body", productDesc);
		paramMap.put("out_trade_no", orderNum);
		paramMap.put("total_fee", String.valueOf(payAmount));
		paramMap.put("spbill_create_ip", PayUtil.getLocalIp());
		paramMap.put("notify_url", "https://yz.suimeikeji.com/api/v1/payment/callback");
		paramMap.put("trade_type", tradeType);
		paramMap.put("openid", openid);
		//
		paramMap.put("sign", PayUtil.createSign(paramMap, XCX_KEY));
		//转换为xml
		String xmlData = PayUtil.mapToXml(paramMap);
		//请求微信后台，获取预支付ID

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_XML);
		HttpEntity<String> formEntity = new HttpEntity<>(xmlData, headers);
		String resXml = restTemplate.postForObject(WX_PAY_UNIFIED_ORDER, xmlData, String.class);
		LOGGER.info("【小程序支付】 统一下单响应：\n" + PayUtil.xmlStrToMap(resXml));
		return PayUtil.xmlStrToMap(resXml);
	}

	/**
	 * 添加或更新支付记录
	 */
	public int addOrUpdatePaymentRecord(String orderNo, int payAmount) throws Exception {
		//写自己的添加或更新支付记录的业务代码
		return 1;
	}

	//https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=9_16&index=10 退款成功回调
	public String refund(SortedMap<String, String> data)  {
		String url = "https://api.mch.weixin.qq.com/secapi/pay/refund";
		data.put("appid", XCX_APP_ID);
		data.put("mch_id", XCX_MCH_ID);
		data.put("nonce_str", PayUtil.makeUUID(32));
		data.put("notify_url", "https://yz.suimeikeji.com/api/v1/payment/refund/callback");
		data.put("sign", PayUtil.createSign(data, XCX_KEY));

		Map<String, String> resp = null;
		try {
			resp = TransferRestTemplate(url, PayUtil.mapToXml(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.println(resp);
		String return_code = resp.get("return_code");   //返回状态码
		String return_msg = resp.get("return_msg");     //返回信息

		String resultReturn = null;
		if ("SUCCESS".equals(return_code)) {
			String result_code = resp.get("result_code");       //业务结果
			String err_code_des = resp.get("err_code_des");     //错误代码描述
			if ("SUCCESS".equals(result_code)) {
				//表示退款申请接受成功，结果通过退款查询接口查询
				//修改用户订单状态为退款申请中（暂时未写）
				resultReturn = "退款申请成功";
			} else {
				LOGGER.info("订单号:{}错误信息:{}", err_code_des);
				resultReturn = err_code_des;
			}
		} else {
			LOGGER.info("订单号:{}错误信息:{}", return_msg);
			resultReturn = return_msg;
		}
		return JSON.toJSONString(resultReturn);
	}

	public Map<String, String> TransferRestTemplate(String url,String data) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File("/Users/wanli.zhou/cert/apiclient_cert.p12"));
		keyStore.load(instream, XCX_MCH_ID.toCharArray());
		// Trust own CA and all self-signed certs
		SSLContext sslcontext = SSLContextBuilder.create()
				.loadKeyMaterial(keyStore, XCX_MCH_ID.toCharArray())
				.build();

		// Allow TLSv1 protocol only
		HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,  new String[]{"TLSv1"},
				null,hostnameVerifier);

		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();

		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpclient);
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);

		StringHttpMessageConverter utf8 = new StringHttpMessageConverter(Charset.forName("utf8"));
		utf8.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.TEXT_XML,MediaType.TEXT_PLAIN,MediaType.APPLICATION_FORM_URLENCODED}));

		ByteArrayHttpMessageConverter b = new ByteArrayHttpMessageConverter();
		b.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_FORM_URLENCODED}));
		restTemplate.getMessageConverters().add(utf8);
		restTemplate.getMessageConverters().add(b);
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.add("Connection", "keep-alive");
		requestHeaders.add("Accept", "*/*");
//		requestHeaders.add("Content-Type", "text/xml; charset=UTF-8");
		requestHeaders.add("Host", "api.mch.weixin.qq.com");
		requestHeaders.add("X-Requested-With", "XMLHttpRequest");
//		requestHeaders.add("Cache-Control", "max-age=0");
//		requestHeaders.add("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");

		org.springframework.http.HttpEntity<String> requestEntity =
				new  org.springframework.http.HttpEntity(data);

//		String sttr = restTemplate.postForObject(url, data, String.class);
		String sttr1 = restTemplate.postForObject(url, requestEntity, String.class);
//		String sttr1 = response.getBody();
		return PayUtil.xmlStrToMap(sttr1);

	}
}