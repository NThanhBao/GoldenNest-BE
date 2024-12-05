package com.example.GoldenNest.model.entity;

import com.example.GoldenNest.model.entity.Enum.MediaType;
import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "product_media")
public class ProductMedia {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String base_name;

    @Column(nullable = false)
    private String public_url;

    @Column(name = "created_at")
    private Timestamp createAt;

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }
    // Getters and Setters
}
