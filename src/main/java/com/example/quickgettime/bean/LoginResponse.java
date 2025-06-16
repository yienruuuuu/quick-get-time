package com.example.quickgettime.bean;

/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
public record LoginResponse(
        boolean success,
        LoginResponseData data
) {
}
