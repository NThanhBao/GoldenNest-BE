package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.service.ProductCouponService;
import com.example.GoldenNest.util.annotation.CheckAdmin;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/product-coupon")
public class ProductCouponController {

    private final ProductCouponService productCouponService;

    public ProductCouponController(ProductCouponService productCouponService) {
        this.productCouponService = productCouponService;
    }

    @CheckLogin
    @CheckAdmin
    @PostMapping("/{productId}/{couponId}")
    public ResponseEntity<Product> addCouponToProduct(@PathVariable("productId") String productId,
                                                      @PathVariable("couponId") String couponId) {
        try {
            Product updatedProduct = productCouponService.addCouponToProduct(productId, couponId);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @CheckLogin
    @CheckAdmin
    @DeleteMapping("/{productId}/{couponId}")
    public ResponseEntity<String> removeCouponFromProduct(@PathVariable String productId,
                                                          @PathVariable String couponId) {
        try {
            productCouponService.removeCouponFromProduct(productId, couponId);
            return new ResponseEntity<>("Coupon removed from product", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{couponId}")
    public ResponseEntity<Page<Product>> getProductsByCouponId(
            @PathVariable String couponId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productCouponService.getProductsByCouponId(couponId, pageable);

        return ResponseEntity.ok(products);
    }
}
