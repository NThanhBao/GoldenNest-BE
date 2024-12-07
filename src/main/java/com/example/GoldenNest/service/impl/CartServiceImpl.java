package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Cart;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.CartRepository;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UsersRepository usersRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    public Cart addToCart(CartDTO cartDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        String productId = cartDTO.getProductId();

        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new EntityNotFoundException("Product not found with ID: " + productId);
        }
        Product product = productOpt.get();

        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingCart != null) {

            existingCart.setQuantity(existingCart.getQuantity() + 1);
            logger.info("Updated quantity for product in cart: {}", existingCart.getQuantity());
            return cartRepository.save(existingCart);
        }

        Cart newCart = new Cart();
        newCart.setUser(currentUser);
        newCart.setProduct(product);
        newCart.setQuantity(1);

        logger.info("Added new product to cart: {}", productId);
        return cartRepository.save(newCart);
    }

    @Override
    public Cart decreaseQuantity(CartDTO cartDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        String productId = cartDTO.getProductId();

        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingCart == null) {
            throw new EntityNotFoundException("Product not found in cart with ID: " + productId);
        }

        int newQuantity = existingCart.getQuantity() - 1;
        if (newQuantity <= 0) {

            cartRepository.delete(existingCart);
            logger.info("Removed product from cart: {}", productId);
            return null;
        }

        existingCart.setQuantity(newQuantity);
        logger.info("Decreased quantity for product in cart: {}", newQuantity);
        return cartRepository.save(existingCart);
    }

    @Override
    public Page<CartDTO> getCartForCurrentUser(Pageable pageable) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Tìm người dùng từ username
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        Page<Cart> cartPage = cartRepository.findByUserId(currentUser.getId(), pageable);

        return cartPage.map(cart -> {
            CartDTO dto = new CartDTO();
            dto.setProductId(cart.getProduct().getId());
            dto.setQuantity(cart.getQuantity());
            return dto;
        });
    }

    @Override
    public int getCartItemCount() {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        Page<Cart> userCart = cartRepository.findByUserId(userId, Pageable.unpaged());

        if (userCart.isEmpty()) {
            return 0;
        }

        int totalQuantity = userCart.getContent().stream()
                .mapToInt(Cart::getQuantity)
                .sum();

        logger.info("Total cart item count for user {}: {}", userId, totalQuantity);
        return totalQuantity;
    }

    @Override
    public void clearCartForCurrentUser() {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        List<Cart> userCart = cartRepository.findByUserId(userId, Pageable.unpaged()).getContent();
        if (userCart.isEmpty()) {
            logger.info("Cart is empty for user {}", userId);
            return;  // Nếu giỏ hàng trống, không cần làm gì thêm
        }

        cartRepository.deleteAll(userCart);
        logger.info("All items removed from cart for user {}", userId);
    }

}
