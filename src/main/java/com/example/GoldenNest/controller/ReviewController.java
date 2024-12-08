package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.ReviewDTO;
import com.example.GoldenNest.service.ReviewService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @CheckLogin
    @PostMapping("/add/{orderId}/{productId}")
    public ResponseEntity<String> addReview(@PathVariable String orderId,
                                            @PathVariable String productId,
                                            @RequestBody ReviewDTO reviewDTO) {
        try {
            reviewService.addReview(orderId, productId, reviewDTO);
            return ResponseEntity.ok("Đánh giá thành công.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
