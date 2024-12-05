package com.example.GoldenNest.repositories;

import com.example.GoldenNest.model.entity.ProductMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductMediaRepository extends JpaRepository<ProductMedia, String> {
}
