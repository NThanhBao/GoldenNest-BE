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
    @PostMapping("/create")
    public ResponseEntity<Cart> addToCart(@RequestParam String productId) {

        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(productId);
        Cart updatedCart = cartService.addToCart(cartDTO);
        return ResponseEntity.ok(updatedCart);
    }

    @CheckLogin
    @DeleteMapping("/decrease")
    public ResponseEntity<Cart> decreaseQuantity(@RequestParam String productId) {

        CartDTO cartDTO = new CartDTO();
        cartDTO.setProductId(productId);
        Cart updatedCart = cartService.decreaseQuantity(cartDTO);
        if (updatedCart == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(updatedCart);
    }

    @CheckLogin
    @GetMapping("/cart/item-count")
    public ResponseEntity<Integer> getCartItemCount() {
        int itemCount = cartService.getCartItemCount();
        return ResponseEntity.ok(itemCount);
    }

    @CheckLogin
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart() {
        cartService.clearCartForCurrentUser();
        return ResponseEntity.ok("Cart cleared successfully");
    }

}
