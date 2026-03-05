package com.miao.ai_gen_web.langgraph4j.ai;

import com.miao.ai_gen_web.utils.SpringContextUtil;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CodeQualityCheckServiceFactory {
    /**
     * 创建代码质量检查 AI 服务
     */
    @Bean
    public CodeQualityCheckService createCodeQualityCheckService() {
        ChatModel chatModel = SpringContextUtil.getBean("codeCMPrototype",ChatModel.class);
        return AiServices.builder(CodeQualityCheckService.class)
                .chatModel(chatModel)
                .build();
    }
}
