package com.example.GoldenNest.model.entity;

import com.example.GoldenNest.model.entity.Enum.OrderStatus;
import com.example.GoldenNest.model.entity.Enum.UserOrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice;

    // Dành cho admin để thông báo
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    // Dành cho user để xác nhận đã đặt hoặc hủy đơn hàng
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserOrderStatus userOrderStatus;

    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
}
