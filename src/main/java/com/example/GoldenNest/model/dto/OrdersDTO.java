package com.example.GoldenNest.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrdersDTO {
    // Getters and Setters
    private String userId;
    private String shippingAddress;
    private List<OrderItemDTO> items;

    // Nested static class for order items
    @Setter
    @Getter
    public static class OrderItemDTO {
        // Getters and Setters
        private String productId;
        private Integer quantity;

    }
}
