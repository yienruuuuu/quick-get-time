package com.example.quickgettime.bean;

/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
public record LoginRequestDTO(
        String username,
        String password,
        String passwd,
        String athena_id,
        String comp_id,
        String locale,
        boolean isRememberMe,
        String captchaToken,
        String captcha
) {
}
