package com.example.GoldenNest.service;

import com.example.GoldenNest.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FavoritesService {

    void addFavorite(String productId);

    void removeFavorite(String productId);

    Page<Product> getUserFavorites(Pageable pageable);

    Integer countFavorites();

    void clearFavorites();
}
