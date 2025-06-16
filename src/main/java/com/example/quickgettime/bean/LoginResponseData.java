package com.example.quickgettime.bean;

/**
 * @author Eric.Lee
 * Date: 2025/6/16
 */
public record LoginResponseData(
        String accessToken,
        String refreshToken,
        String uuid,
        int athena_id,
        String comp_cod,
        String hotel_cod,
        String rcHotelCod,
        String group_id,
        String func_comp_cod,
        String func_hotel_cod,
        String user_id,
        String username,
        String locale,
        Object cashierClosure
) {
}
