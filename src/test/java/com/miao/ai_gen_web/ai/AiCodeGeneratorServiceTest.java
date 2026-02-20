package com.miao.ai_gen_web.ai;

import com.miao.ai_gen_web.ai.model.HtmlCodeResult;
import com.miao.ai_gen_web.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("做个登录界面");
        Assertions.assertNotNull(result);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult result= aiCodeGeneratorService.generateMultiFileCode("做个最简单的登录界面");
        Assertions.assertNotNull(result);
    }
}
