package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCouponRepository extends JpaRepository<Product, String> {


}
