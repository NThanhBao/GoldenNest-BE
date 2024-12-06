package com.example.GoldenNest.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDTO {

    private String id;

    private String name;

    private String description;

    private Double price;

    private Integer stockQuantity;

    private String categoryId;

    private List<String> mediasId;
}
