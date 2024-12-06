package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Coupon;
import com.example.GoldenNest.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
}