package com.sm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 */
@SpringBootApplication
@Component("com.sm")
@EnableScheduling
public class YzApplication {
	@Value("${rest.connection.timeout:6000}")
	private Integer connectionTimeout;
	// 信息读取超时时间
	@Value("${rest.read.timeout:6000}")
	private Integer readTimeout;

	@Value("${q.n.k}")
	String accessKey ;
	@Value("${q.n.s}")
	String secretKey;

	@Bean
	public RestTemplate registerTemplate() {
		RestTemplate restTemplate = new RestTemplate(getFactory());
		//这个地方需要配置消息转换器，不然收到消息后转换会出现异常
		restTemplate.setMessageConverters(getConverts());
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
