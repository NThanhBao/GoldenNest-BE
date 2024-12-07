package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<Product> getAllProducts(Pageable pageable);

    Product createPosts(ProductDTO post);

    Product updateProduct(String productId, ProductDTO productDTO);

    void deleteProduct(String productId);

    Page<Product> getProductsByCategoryId(String categoryId, Pageable pageable);
}
