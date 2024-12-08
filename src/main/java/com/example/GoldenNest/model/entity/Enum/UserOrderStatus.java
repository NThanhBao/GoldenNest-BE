package com.example.GoldenNest.model.entity.Enum;

import lombok.Getter;

@Getter
public enum UserOrderStatus {
    PLACED("Đã đặt hàng"),
    CANCELLED("Đã hủy");

    private final String description;

    // Constructor để gán mô tả cho trạng thái của người dùng
    UserOrderStatus(String description) {
        this.description = description;
    }
}