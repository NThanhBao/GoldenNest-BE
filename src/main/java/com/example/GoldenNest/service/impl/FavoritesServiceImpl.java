package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.FavoritesService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final UsersRepository usersRepository;
    private final ProductRepository productRepository;
    private static final Logger logger = LoggerFactory.getLogger(FavoritesServiceImpl.class);

    @Override
    public void addFavorite(String productId) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        // Tìm người dùng trong cơ sở dữ liệu
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        logger.info("Current user ID: {}", currentUser.getId());

        // Tìm sản phẩm trong cơ sở dữ liệu
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra xem sản phẩm đã nằm trong danh sách yêu thích hay chưa
        if (!currentUser.getFavoritesProduct().contains(product)) {
            currentUser.getFavoritesProduct().add(product); // Thêm sản phẩm vào danh sách yêu thích
            usersRepository.save(currentUser); // Lưu thay đổi của người dùng
            logger.info("Product {} added to favorites for user {}", productId, currentUser.getUsername());
        } else {
            logger.info("Product {} is already in the favorites for user {}", productId, currentUser.getUsername());
        }
    }


    @Override
    public void removeFavorite(String productId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        // Kiểm tra xem sản phẩm có trong danh sách yêu thích không
        if (!currentUser.getFavoritesProduct().contains(product)) {
            throw new IllegalArgumentException("Product is not in the favorites");
        }

        // Xóa sản phẩm khỏi danh sách yêu thích
        currentUser.getFavoritesProduct().remove(product);
        usersRepository.save(currentUser);
        logger.info("Product {} removed from favorites for user {}", productId, currentUser.getId());
    }



    @Override
    public Page<Product> getUserFavorites(Pageable pageable) {
        // Lấy thông tin người dùng đang đăng nhập
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        // Tìm người dùng trong cơ sở dữ liệu
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }
        logger.info("Fetching favorite products for user ID: {}", currentUser.getId());

        // Lấy danh sách sản phẩm yêu thích và phân trang
        List<Product> favoriteProducts = currentUser.getFavoritesProduct();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), favoriteProducts.size());

        if (start > favoriteProducts.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, favoriteProducts.size());
        }

        List<Product> paginatedProducts = favoriteProducts.subList(start, end);
        return new PageImpl<>(paginatedProducts, pageable, favoriteProducts.size());
    }

    @Override
    public Integer countFavorites() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        return currentUser.getFavoritesProduct().size();
    }

    @Override
    public void clearFavorites() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        Users currentUser = usersRepository.findByUsername(currentUsername);
        if (currentUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        currentUser.getFavoritesProduct().clear();
        usersRepository.save(currentUser);
    }


}
