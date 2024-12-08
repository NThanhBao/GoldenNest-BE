package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.ReviewDTO;

public interface ReviewService {
    void addReview(String orderId, String productId, ReviewDTO reviewDTO);
}
