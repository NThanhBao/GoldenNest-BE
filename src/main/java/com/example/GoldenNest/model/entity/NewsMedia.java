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
public class NewsMedia {
    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "news_id")
    private News news;

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

    public NewsMedia() {
        this.id = UUID.randomUUID().toString();  // Khởi tạo UUID thủ công
    }
    // Getters and Setters
}
