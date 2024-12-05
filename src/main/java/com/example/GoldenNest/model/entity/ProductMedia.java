package com.example.GoldenNest.model.entity;

import com.example.GoldenNest.model.entity.Enum.MediaType;
import jakarta.persistence.*;

@Entity
@Table(name = "product_media")
public class ProductMedia {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;

    // Getters and Setters
}
