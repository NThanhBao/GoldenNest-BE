package com.example.GoldenNest.model.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductDTO {
    private String id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private String category;
    private String imageUrl;
    private MultipartFile image;  // File ảnh sản phẩm

    // Getters and Setters
}
