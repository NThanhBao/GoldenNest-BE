package com.example.GoldenNest.controller;


import com.example.GoldenNest.model.dto.CartDTO;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.service.FavoritesService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    @CheckLogin
    @PostMapping("/{productId}")
    public ResponseEntity<?> addFavorite(@PathVariable String productId) {
        favoritesService.addFavorite(productId);
        return ResponseEntity.ok("Product added to favorites");
    }

    @CheckLogin
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFavorite(@PathVariable String productId) {
        try {
            favoritesService.removeFavorite(productId);
            return ResponseEntity.ok("Product removed from favorites");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }


    @CheckLogin
    @GetMapping
    public ResponseEntity<Page<Product>> getFavorites(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> favorites = favoritesService.getUserFavorites(pageable);
        return ResponseEntity.ok(favorites);
    }

    @CheckLogin
    @GetMapping("/count")
    public ResponseEntity<Integer> countFavorites() {
        int count = favoritesService.countFavorites();
        return ResponseEntity.ok(count);
    }

    @CheckLogin
    @DeleteMapping
    public ResponseEntity<?> clearFavorites() {
        favoritesService.clearFavorites();
        return ResponseEntity.ok("All favorites cleared");
    }

}
