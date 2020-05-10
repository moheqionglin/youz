package com.sm.config;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger相关配置类
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    private Contact contact = new Contact("willy","localhost:8080/swagger-ui.html", "xxx@yeah.net");

    private Tag[] getTags() {
        return new Tag[]{
            new Tag("profile", "profile相关"),
            new Tag("makemoney", "赚钱页面接口"),
            new Tag("catalog", "商品目录管理，包含特价商品目录"),
            new Tag("product", "product相关页面接口"),
            new Tag("shoppingcart", "购物车相关页面接口"),
            new Tag("adminOrder", "admin的order相关页面接口"),
            new Tag("order", "非admin的order相关页面接口"),
            new Tag("adminother", "admin其他接口"),
            new Tag("toutiao", "头条管理"),
            new Tag("tixian", "提现管理"),
            new Tag("lunbo", "轮播"),
            new Tag("search", "搜索"),
            new Tag("comment", "评论"),
            new Tag("supplier", "供应商"),
            new Tag("pay", "支付"),
            new Tag("message", "消息"),
        };
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build()
                .apiInfo(apiInfo())
                .tags(new Tag("auth", "认证相关接口"), getTags())
                .securitySchemes(Lists.newArrayList(apiKey()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("平台接口 v1.0")
                .description("平台接口")
                .contact(contact)
                .version("1.0")
                .build();
    }
    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
}
