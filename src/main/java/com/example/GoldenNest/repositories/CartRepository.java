package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CartRepository extends JpaRepository<Cart, String> {

    Page<Cart> findByUserId(String userId, Pageable pageable);

    Cart findByUserIdAndProductId(String userId, String productId);

}