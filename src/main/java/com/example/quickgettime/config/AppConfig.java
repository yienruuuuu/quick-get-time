package com.example.quickgettime.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 配置類
 *
 * @author Eric.Lee
 * Date:2024/6/28
 */
@Configuration
@Getter
public class AppConfig {

    @Value("${system.url}")
    private String systemDomain;

    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @Value("${tesseract-path}")
    private String tesseractPath;

    //單例 ObjectMapper 物件
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 秒內連不上就 timeout
        factory.setReadTimeout(20000);   // 成功連上後最多等 20 秒
        return new RestTemplate(factory);
    }

    @Bean
    public Tesseract tesseract() {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tesseractPath);
        tesseract.setLanguage("eng");
        tesseract.setVariable("tessedit_char_whitelist", "0123456789");
        tesseract.setVariable("classify_bln_numeric_mode", "1");
        tesseract.setPageSegMode(7);
        tesseract.setOcrEngineMode(1);
        return tesseract;
    }
}
