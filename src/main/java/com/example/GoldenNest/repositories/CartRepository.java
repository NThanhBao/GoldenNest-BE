package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    List<Cart> findByUserId(String userId);

    Cart findByUserIdAndProductId(String userId, String productId);
}