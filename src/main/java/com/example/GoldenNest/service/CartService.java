package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CartService {

    Cart addToCart(CartDTO cartDTO);

    Cart decreaseQuantity(CartDTO cartDTO);

    Page<CartDTO> getCartForCurrentUser(Pageable pageable);

    int getCartItemCount();

    void clearCartForCurrentUser();
}

