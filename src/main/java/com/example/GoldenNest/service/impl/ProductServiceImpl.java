package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Category;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.ProductMedia;
import com.example.GoldenNest.model.entity.Users;
import com.example.GoldenNest.repositories.CategoryRepository;
import com.example.GoldenNest.repositories.ProductMediaRepository;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.repositories.UsersRepository;
import com.example.GoldenNest.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CategoryRepository categoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UsersRepository usersRepository, ProductMediaRepository productMediaRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.usersRepository = usersRepository;
        this.productMediaRepository = productMediaRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        if (products.isEmpty()) {
            logger.warn("No products found.");
        }
        return products;
    }

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
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setUserId(currentUser);

        Optional<Category> categoryOptional = categoryRepository.findById(productDTO.getCategoryId());
        if (categoryOptional.isPresent()) {
            product.setCategory(categoryOptional.get());
        } else {
            throw new EntityNotFoundException("Category not found with ID: " + productDTO.getCategoryId());
        }

        List<ProductMedia> medias = new ArrayList<>();
        for (String mediaId : productDTO.getMediasId()) {
            Optional<ProductMedia> mediaOptional = productMediaRepository.findById(mediaId);
            if (mediaOptional.isPresent()) {
                ProductMedia media = mediaOptional.get();

                media.setProduct(product);
                medias.add(media);
            }
        }
        product.setMedias(medias);

        Product createdProduct = productRepository.save(product);
        logger.info("New product created with ID: {}", createdProduct.getId());

        return createdProduct;
    }

    @Override
    public Product updateProduct(String productId, ProductDTO productDTO) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStockQuantity(productDTO.getStockQuantity());

            Optional<Category> categoryOptional = categoryRepository.findById(productDTO.getCategoryId());
            categoryOptional.ifPresent(product::setCategory);

            List<ProductMedia> medias = new ArrayList<>();
            for (String mediaId : productDTO.getMediasId()) {
                Optional<ProductMedia> mediaOptional = productMediaRepository.findById(mediaId);
                mediaOptional.ifPresent(media -> {
                    media.setProduct(product);
                    medias.add(media);
                });
            }
            product.setMedias(medias);

            Product updatedProduct = productRepository.save(product);
            logger.info("Product with ID: {} updated successfully", updatedProduct.getId());

            return updatedProduct;
        } else {
            throw new EntityNotFoundException("Product not found with ID: " + productId);
        }
    }


    @Override
    public void deleteProduct(String productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            productRepository.deleteById(productId);
            logger.info("Product with ID: {} deleted successfully", productId);
        } else {
            throw new EntityNotFoundException("Product not found with ID: " + productId);
        }
    }

    @Override
    public Page<Product> getProductsByCategoryId(String categoryId, Pageable pageable) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()) {
            throw new EntityNotFoundException("Category not found with ID: " + categoryId);
        }

        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        if (products.isEmpty()) {
            logger.warn("No products found for Category ID: {}", categoryId);
        }

        return products;
    }


    @Override
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        // Sử dụng phương thức tìm kiếm trong repository để tìm sản phẩm theo tên
        Page<Product> products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        if (products.isEmpty()) {
            logger.warn("No products found with name: {}", name);
        }
        return products;
    }

}