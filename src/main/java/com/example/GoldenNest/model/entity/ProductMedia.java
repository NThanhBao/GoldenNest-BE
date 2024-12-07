package com.example.GoldenNest.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "product_media")
public class ProductMedia {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "base_name")
    private String base_name;

    @Column(name = "public_url")
    private String public_url;

    @Column(name = "created_at")
    private Timestamp createAt;

    @PrePersist
    protected void onCreate() {
        createAt = new Timestamp(new Date().getTime());
    }

    public ProductMedia() {
        this.id = UUID.randomUUID().toString();  // Khởi tạo UUID thủ công
    }
    // Getters and Setters
}
