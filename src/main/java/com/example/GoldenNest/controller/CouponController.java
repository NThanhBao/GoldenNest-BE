package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.CouponDTO;
import com.example.GoldenNest.model.entity.Coupon;
import com.example.GoldenNest.service.CouponService;
import com.example.GoldenNest.util.annotation.CheckAdmin;
import com.example.GoldenNest.util.annotation.CheckLogin;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @CheckLogin
    @CheckAdmin
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@Valid @RequestBody CouponDTO couponDTO) {
        Coupon createdCoupon = couponService.createCoupon(couponDTO);
        return ResponseEntity.ok(createdCoupon);
    }

    @CheckLogin
    @CheckAdmin
    @GetMapping
    public Page<Coupon> getAllCoupons(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return couponService.getAllCoupons(pageable);
    }

    @CheckLogin
    @CheckAdmin
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable String id) {
        Coupon coupon = couponService.getCouponById(id);
        return ResponseEntity.ok(coupon);
    }

    @CheckLogin
    @CheckAdmin
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(
            @PathVariable String id,
            @Valid @RequestBody CouponDTO couponDTO) {
        Coupon updatedCoupon = couponService.updateCoupon(id, couponDTO);
        return ResponseEntity.ok(updatedCoupon);
    }

    @CheckLogin
    @CheckAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}
