package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Cart;
import com.example.GoldenNest.service.CartService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @CheckLogin
    @GetMapping("/")
    public Page<CartDTO> getCartForCurrentUser(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return cartService.getCartForCurrentUser(pageable);
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
