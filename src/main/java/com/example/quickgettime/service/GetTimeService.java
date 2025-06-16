package com.example.quickgettime.service;

import com.example.quickgettime.bean.CaptchaDTO;
import com.example.quickgettime.bean.LoginDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
@Service("getTimeService")
public class GetTimeService {

    private final CaptchaService captchaService;
    private final LoginService loginService;
    private final QueryCheckInTimeService queryCheckInTimeService;
    private static final int MAX_RETRY = 5;

    public GetTimeService(CaptchaService captchaService, LoginService loginService, QueryCheckInTimeService queryCheckInTimeService) {
        this.captchaService = captchaService;
        this.loginService = loginService;
        this.queryCheckInTimeService = queryCheckInTimeService;
    }

    public void getTime() {
        LoginDTO dto = null;

        for (int attempt = 1; attempt <= MAX_RETRY; attempt++) {
            System.out.printf("第 %d 次嘗試登入...\n", attempt);

            // 獲取驗證碼數值
            CaptchaDTO captcha = captchaService.getCaptchaToken();

            // 使用驗證碼進行登入
            dto = loginService.login(captcha.captchaToken(), captcha.captcha());
            System.out.println("登入結果：" + dto);

            if (dto != null && dto.success()) {
                break;
            }
        }

        if (dto == null || !dto.success()) {
            System.err.println("登入失敗，已達最大重試次數");
            return;
        }

        // 獲取打卡紀錄
        Optional<String> record = queryCheckInTimeService.fetchAttendanceInfo(dto.token());
        if (record.isPresent()) {
            System.out.println("打卡紀錄：\n" + record.get());
        } else {
            System.out.println("無法取得打卡紀錄");
        }
    }
}