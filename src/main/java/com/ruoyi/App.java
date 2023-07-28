package com.ruoyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("接口文档地址: http://localhost:808/doc.html");
    }
}
