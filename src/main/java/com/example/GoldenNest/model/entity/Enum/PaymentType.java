package com.example.GoldenNest.model.entity.Enum;

import lombok.Getter;

@Getter
public enum PaymentType {
    CREDIT_CARD("Thẻ tín dụng"),
    PAYPAL("PayPal"),
    CASH("Tiền mặt"),
    PAID("Đã thanh toán");

    private final String description;

    PaymentType(String description) {
        this.description = description;
    }
}
