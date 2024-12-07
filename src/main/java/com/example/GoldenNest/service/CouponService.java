package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.CouponDTO;
import com.example.GoldenNest.model.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CouponService {
    Coupon createCoupon(CouponDTO couponDTO);

    Page<Coupon> getAllCoupons(Pageable pageable);

    Coupon getCouponById(String id);

    Coupon updateCoupon(String id, CouponDTO couponDTO);

    void deleteCoupon(String id);
}