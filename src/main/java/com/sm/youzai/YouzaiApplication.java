package com.sm.youzai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@ComponentScan(basePackages = "com.sm")
public class YouzaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(YouzaiApplication.class, args);
    }

}
