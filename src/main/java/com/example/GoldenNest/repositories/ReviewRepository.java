package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String>{
}
