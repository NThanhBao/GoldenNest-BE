package com.example.GoldenNest.service;

import com.example.GoldenNest.model.entity.Coupon;
import com.example.GoldenNest.model.entity.Product;

import java.util.List;

public interface ProductCouponService {

    Product addCouponToProduct(String productId, String couponId);

    void removeCouponFromProduct(String productId, String couponId);
    // Thêm các phương thức khác nếu cần (ví dụ: getProductById, getAllProducts, v.v.)
}