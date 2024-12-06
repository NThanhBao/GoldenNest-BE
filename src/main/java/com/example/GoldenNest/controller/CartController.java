package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Cart;
import com.example.GoldenNest.service.CartService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @CheckLogin
    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(@RequestBody CartDTO cartDTO) {
        Cart updatedCart = cartService.addToCart(cartDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @CheckLogin
    @DeleteMapping("/decrease")
    public ResponseEntity<Cart> decreaseQuantity(@RequestBody CartDTO cartDTO) {
        Cart updatedCart = cartService.decreaseQuantity(cartDTO);
        if (updatedCart == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(updatedCart);
    }
}
