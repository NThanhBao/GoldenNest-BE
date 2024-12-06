package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Cart;

public interface CartService {

    Cart addToCart(CartDTO cartDTO);

    Cart decreaseQuantity(CartDTO cartDTO);

}

