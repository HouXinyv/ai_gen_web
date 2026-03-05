package com.miao.ai_gen_web.langgraph4j.ai;

import com.miao.ai_gen_web.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageCollectionPlanServiceFactory {

    @Bean
    public ImageCollectionPlanService createImageCollectionPlanService() {
        ChatModel chatModel = SpringContextUtil.getBean("codeCMPrototype",ChatModel.class);
        return AiServices.builder(ImageCollectionPlanService.class)
                .chatModel(chatModel)
                .build();
    }
}
