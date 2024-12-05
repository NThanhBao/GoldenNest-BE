package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;

public interface ProductService {

    Product createPosts(ProductDTO post);

}
