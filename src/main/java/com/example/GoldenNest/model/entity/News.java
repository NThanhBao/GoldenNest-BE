package com.example.GoldenNest.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "news")
public class News {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "source", length = 255)
    private String source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private Users userId;

    @JsonManagedReference
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<NewsMedia> medias;

    // Constructor không tham số
    public News() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    // Getters and Setters
}
