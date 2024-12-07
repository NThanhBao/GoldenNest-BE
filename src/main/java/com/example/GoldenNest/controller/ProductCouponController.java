package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.service.ProductCouponService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductCouponController {

    private final ProductCouponService productCouponService;

    public ProductCouponController(ProductCouponService productCouponService) {
        this.productCouponService = productCouponService;
    }

    @CheckLogin
    @PostMapping("/{productId}/add-coupon/{couponId}")
    public ResponseEntity<Product> addCouponToProduct(@PathVariable("productId") String productId,
                                                      @PathVariable("couponId") String couponId) {
        try {
            // Gọi service để thêm coupon vào sản phẩm
            Product updatedProduct = productCouponService.addCouponToProduct(productId, couponId);
            return ResponseEntity.ok(updatedProduct);  // Trả về sản phẩm đã cập nhật
        } catch (RuntimeException e) {
            // Trường hợp không tìm thấy sản phẩm hoặc coupon
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @CheckLogin
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeCouponFromProduct(@RequestParam String productId, @RequestParam String couponId) {
        try {
            productCouponService.removeCouponFromProduct(productId, couponId);
            return new ResponseEntity<>("Coupon removed from product", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/products/{couponId}")
    public ResponseEntity<Page<Product>> getProductsByCouponId(
            @PathVariable String couponId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productCouponService.getProductsByCouponId(couponId, pageable);

        return ResponseEntity.ok(products);
    }
}
