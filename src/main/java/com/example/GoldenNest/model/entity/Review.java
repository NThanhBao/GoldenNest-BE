package com.example.GoldenNest.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "reviews")
public class Review {
    @Id
    @Column(length = 36)
    @NotNull
    @EqualsAndHashCode.Include
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
    private Timestamp createdAt;

    public Review() {
        this.id = UUID.randomUUID().toString();  // Khởi tạo UUID thủ công
    }
    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(new Date().getTime());
    }
    // Getters and Setters
}
