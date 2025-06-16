package com.example.quickgettime.service;

import com.example.quickgettime.bean.LoginDTO;
import com.example.quickgettime.bean.LoginRequestDTO;
import com.example.quickgettime.bean.LoginResponse;
import com.example.quickgettime.config.AppConfig;
import com.example.quickgettime.infrastructure.RestTemplateClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
@Service("loginService")
public class LoginService {
    private final RestTemplateClient restTemplate;
    private final AppConfig appConfig;

    public LoginService(RestTemplateClient restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }


    public LoginDTO login(String captchaToken, String captcha) {
        String url = appConfig.getSystemDomain() + "/validate/login";
        LoginDTO loginDTO = null;

        LoginRequestDTO request = new LoginRequestDTO(
                "a23034",
                "s8903132",
                "s8903132",
                "102",
                "athena",
                "zh_TW",
                false,
                captchaToken,
                captcha
        );

        try {
            LoginResponse response = restTemplate.post(
                    url,
                    request,
                    new ParameterizedTypeReference<>() {
                    }
            );
            System.out.println("登入回應：" + response);
            loginDTO = new LoginDTO(response.success(), response.data().accessToken());
        } catch (Exception e) {
            System.err.println("登入失敗：" + e.getMessage());
        }
        return loginDTO;
    }
}
