package com.sm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.sm.third.yilianyun.LYYService;
import com.sm.utils.wx.AesException;
import com.sm.utils.wx.WXBizMsgCrypt;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 */
@SpringBootApplication
@Component("com.sm")
@EnableScheduling
public class YzApplication {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	@Value("${rest.connection.timeout:6000}")
	private Integer connectionTimeout;
	// 信息读取超时时间
	@Value("${rest.read.timeout:6000}")
	private Integer readTimeout;

	@Value("${q.n.k}")
	String accessKey ;
	@Value("${q.n.s}")
	String secretKey;
	@Value("${sm.wx.appid}")
	public  String XCX_APP_ID;
	@Value("${sm.wx.mid}")
	public  String XCX_MCH_ID;
	@Value("${sm.wx.key}")
	public  String XCX_KEY;

	@Value("${messageTk}")
	private String messageTk;
	@Value("${messageK}")
	private String messageK;
	@Bean
	public WXBizMsgCrypt wXBizMsgCrypt(){
		try {
			return new WXBizMsgCrypt(messageTk, messageK, XCX_APP_ID);
		} catch (AesException e) {
			log.error("", e);
		}
		return null;
	}
	@Bean
	public LYYService lyyService(){
		LYYService lyyService = new LYYService();
		return lyyService;

	}
	@Bean
	@Primary
	public RestTemplate registerTemplate() {
		RestTemplate restTemplate = new RestTemplate(getFactory());
		//这个地方需要配置消息转换器，不然收到消息后转换会出现异常
		restTemplate.setMessageConverters(getConverts());
		return restTemplate;
	}

	@Bean(name = "refountRestTemplate")
	public RestTemplate refountRestTemplate() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
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

		ByteArrayHttpMessageConverter b = new ByteArrayHttpMessageConverter();
		b.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.APPLICATION_FORM_URLENCODED}));
		restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		restTemplate.getMessageConverters().add(b);
		return restTemplate;
	}
	@Bean
	public ObjectMapper objectMapper(){
		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.registerModule(new JodaModule());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//		objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));
//		objectMapper.setDateFormat(new ISO8601DateFormat());
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper;
	}
	private SimpleClientHttpRequestFactory getFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(connectionTimeout);
		factory.setReadTimeout(readTimeout);
		return factory;
	}
	private List<HttpMessageConverter<?>> getConverts() {
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
		MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
		jsonMessageConverter.setObjectMapper(objectMapper());

		MediaType[] mediaTypes = new MediaType[]{
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_OCTET_STREAM,

				MediaType.APPLICATION_JSON_UTF8,
//				MediaType.TEXT_HTML,
//				MediaType.TEXT_PLAIN,
//				MediaType.TEXT_XML,
				MediaType.APPLICATION_STREAM_JSON,
//				MediaType.APPLICATION_ATOM_XML,
				MediaType.APPLICATION_FORM_URLENCODED,
//				MediaType.APPLICATION_PDF,
		};

		jsonMessageConverter.setSupportedMediaTypes(Arrays.asList(mediaTypes));

		messageConverters.add(jsonMessageConverter);
		StringHttpMessageConverter utf8 = new StringHttpMessageConverter(Charset.forName("utf8"));
		utf8.setSupportedMediaTypes(Arrays.asList(new MediaType[]{MediaType.TEXT_XML,MediaType.TEXT_PLAIN,MediaType.APPLICATION_FORM_URLENCODED}));
		messageConverters.add(utf8);
		return messageConverters;
	}

	@Bean
	public Auth qnAuth(){
		Configuration cfg = new Configuration(Region.region0());
		UploadManager uploadManager = new UploadManager(cfg);
		Auth auth = Auth.create(accessKey, secretKey);
		return auth;
	}

	public static void main(String[] args) {
		SpringApplication.run(YzApplication.class, args);
	}
}
