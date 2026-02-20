package com.miao.ai_gen_web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
@MapperScan("com.miao.ai_gen_web.mapper")
public class AiGenWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiGenWebApplication.class, args);
    }

}
