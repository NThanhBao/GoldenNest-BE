package com.example.GoldenNest.model.entity.Enum;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Đang chờ xử lý"),
    IN_TRANSIT("Đang giao hàng"),
    SHIPPED("Đã giao"),
    DELIVERED("Đã giao thành công"),
    CANCELLED("Đã hủy");

    private final String description;

    // Constructor để gán mô tả cho từng trạng thái
    OrderStatus(String description) {
        this.description = description;
    }
}