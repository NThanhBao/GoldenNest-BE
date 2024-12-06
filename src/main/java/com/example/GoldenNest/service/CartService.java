package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    Cart addToCart(CartDTO cartDTO);

    Cart decreaseQuantity(CartDTO cartDTO);

    Page<CartDTO> getCartForCurrentUser(Pageable pageable);
}

