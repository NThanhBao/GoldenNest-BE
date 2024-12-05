package com.example.GoldenNest.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Integer rating;

    private String comment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
}
