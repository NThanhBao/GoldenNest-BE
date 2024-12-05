package com.example.GoldenNest.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    // Getters and Setters
}
