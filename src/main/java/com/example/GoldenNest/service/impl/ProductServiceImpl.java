package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.ProductMedia;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.ProductMediaRepository;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UsersRepository usersRepository;
    private final ProductMediaRepository productMediaRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UsersRepository usersRepository, ProductMediaRepository productMediaRepository) {
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.productMediaRepository = productMediaRepository;
    }

    // Add product and handle image upload
    @Override
    public Product createPosts(ProductDTO productDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        logger.info("Current username: {}", currentUsername);

        Users currentUser = usersRepository.findByUsername(currentUsername);
        String userId = currentUser != null ? currentUser.getId() : null;
        logger.info("Current user ID: {}", userId);

        if (currentUser == null) {
            throw new EntityNotFoundException("Current user not found");
        }

        Product product = new Product();
        product.setId(UUID.randomUUID().toString());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setCategory(productDTO.getCategory());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setUserId(currentUser);

        // Thêm media vào danh sách của bài viết
        List<ProductMedia> medias = new ArrayList<>();
        for (String mediaId : productDTO.getMediasId()) {
            Optional<ProductMedia> mediaOptional = productMediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                ProductMedia media = mediaOptional.get();
                // Cập nhật trường postsId của media
                media.setProduct(product);
                medias.add(media);
            }
        }
        product.setMedias(medias);

        Product createdPost = productRepository.save(product);
        logger.info("New post created with ID: {}", createdPost.getId());

        logger.info("Post creation process completed successfully");

        return createdPost;
    }
}