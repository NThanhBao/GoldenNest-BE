package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.CouponDTO;
import com.example.GoldenNest.model.entity.Coupon;
import com.example.GoldenNest.repositories.CouponRepository;
import com.example.GoldenNest.service.CouponService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @Override
    public Coupon createCoupon(CouponDTO couponDTO) {
        Coupon coupon = new Coupon();
        coupon.setCode(couponDTO.getCode());
        coupon.setDiscountAmount(couponDTO.getDiscountAmount());
        coupon.setExpiryDate(couponDTO.getExpiryDate());
        coupon.setIsActive(couponDTO.getIsActive());
        return couponRepository.save(coupon);
    }

    @Override
    public Page<Coupon> getAllCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }


    @Override
    public Coupon getCouponById(String id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
    }

    @Override
    public Coupon updateCoupon(String id, CouponDTO couponDTO) {
        // Tìm coupon hiện có bằng ID
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));

        // Cập nhật các trường từ DTO
        existingCoupon.setCode(couponDTO.getCode());
        existingCoupon.setDiscountAmount(couponDTO.getDiscountAmount());
        existingCoupon.setExpiryDate(couponDTO.getExpiryDate());
        existingCoupon.setIsActive(couponDTO.getIsActive());

        // Lưu lại thông tin đã cập nhật
        return couponRepository.save(existingCoupon);
    }


    @Override
    public void deleteCoupon(String id) {
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));

        couponRepository.delete(existingCoupon);
    }
}
