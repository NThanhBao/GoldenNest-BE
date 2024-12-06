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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        // Tìm kiếm thông tin người dùng từ username
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        // Lấy productId từ CartDTO
        String productId = cartDTO.getProductId();

        // Kiểm tra sự tồn tại của sản phẩm
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            throw new EntityNotFoundException("Product not found with ID: " + productId);
        }
        Product product = productOpt.get();

        // Kiểm tra nếu sản phẩm đã tồn tại trong giỏ hàng của người dùng
        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingCart != null) {
            // Nếu sản phẩm đã tồn tại, tăng số lượng
            existingCart.setQuantity(existingCart.getQuantity() + 1);
            logger.info("Updated quantity for product in cart: {}", existingCart.getQuantity());
            return cartRepository.save(existingCart);
        }

        // Nếu sản phẩm chưa tồn tại trong giỏ hàng, tạo mới
        Cart newCart = new Cart();
        newCart.setUser(currentUser); // Gán thông tin người dùng
        newCart.setProduct(product);  // Gán sản phẩm
        newCart.setQuantity(1); // Mặc định số lượng là 1

        logger.info("Added new product to cart: {}", productId);
        return cartRepository.save(newCart);
    }

    @Override
    public Cart decreaseQuantity(CartDTO cartDTO) {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        // Tìm kiếm thông tin người dùng từ username
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        String userId = currentUser.getId();
        logger.info("Current user ID: {}", userId);

        // Lấy productId từ CartDTO
        String productId = cartDTO.getProductId();

        // Kiểm tra sự tồn tại của sản phẩm trong giỏ hàng
        Cart existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
        if (existingCart == null) {
            throw new EntityNotFoundException("Product not found in cart with ID: " + productId);
        }

        // Giảm số lượng sản phẩm
        int newQuantity = existingCart.getQuantity() - 1;
        if (newQuantity <= 0) {
            // Xóa sản phẩm khỏi giỏ hàng nếu số lượng <= 0
            cartRepository.delete(existingCart);
            logger.info("Removed product from cart: {}", productId);
            return null; // Trả về null hoặc thông báo khác tùy yêu cầu
        }

        // Cập nhật số lượng sản phẩm
        existingCart.setQuantity(newQuantity);
        logger.info("Decreased quantity for product in cart: {}", newQuantity);
        return cartRepository.save(existingCart);
    }
}
