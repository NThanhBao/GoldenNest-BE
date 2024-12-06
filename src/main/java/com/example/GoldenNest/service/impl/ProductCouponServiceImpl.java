package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.Coupon;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.repositories.*;
import com.example.GoldenNest.service.ProductCouponService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCouponServiceImpl implements ProductCouponService {

    private final CouponRepository couponRepository;
    private final ProductRepository productRepository;
    private final JdbcTemplate jdbcTemplate;  // Sử dụng JdbcTemplate để thao tác với bảng trung gian

    public ProductCouponServiceImpl(CouponRepository couponRepository, ProductRepository productRepository, JdbcTemplate jdbcTemplate) {
        this.couponRepository = couponRepository;
        this.productRepository = productRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Product addCouponToProduct(String productId, String couponId) {
        // Tìm sản phẩm bằng productId
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        // Tìm coupon bằng couponId
        Optional<Coupon> couponOptional = couponRepository.findById(couponId);
        if (couponOptional.isEmpty()) {
            throw new RuntimeException("Coupon not found");
        }

        // Thêm mối quan hệ vào bảng product_coupons
        String sql = "INSERT INTO product_coupons (product_id, coupon_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, productId, couponId);

        // Trả về sản phẩm đã cập nhật
        return productOptional.get();
    }

    @Override
    public void removeCouponFromProduct(String productId, String couponId) {
        // Xóa mối quan hệ giữa productId và couponId trong bảng product_coupons
        String sql = "DELETE FROM product_coupons WHERE product_id = ? AND coupon_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, productId, couponId);

        if (rowsAffected == 0) {
            throw new RuntimeException("No relationship found between Product and Coupon");
        }
    }

}

