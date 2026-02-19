package com.miao.ai_gen_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class AiGenWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiGenWebApplication.class, args);
    }

}
