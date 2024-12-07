package com.example.GoldenNest.service;

import com.example.GoldenNest.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductCouponService {

    Product addCouponToProduct(String productId, String couponId);

    void removeCouponFromProduct(String productId, String couponId);

    Page<Product> getProductsByCouponId(String couponId, Pageable pageable);
}