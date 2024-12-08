package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.ReviewDTO;
import com.example.GoldenNest.model.entity.Enum.OrderStatus;
import com.example.GoldenNest.model.entity.Order;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.Review;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.OrdersRepository;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.repositories.ReviewRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;



@Service
public class ReviewServiceImpl implements ReviewService {

    private final OrdersRepository orderRepository;

    private final ProductRepository productRepository;

    private final UsersRepository usersRepository;

    private final ReviewRepository reviewRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    public ReviewServiceImpl(OrdersRepository orderRepository, ProductRepository productRepository, UsersRepository usersRepository, ReviewRepository reviewRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void addReview(String orderId, String productId, ReviewDTO reviewDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser != null ? currentUser.getId() : null;
        logger.info("Current user ID: {}", userId);

        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không thể đánh giá sản phẩm trong đơn hàng của người khác.");
        }

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Đánh giá thất bại vui lòng liên hệ quản trị viên.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with ID: " + productId));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(currentUser);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        reviewRepository.save(review);

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        logger.info("Review added successfully, and order status updated to DELIVERED.");
    }

}
