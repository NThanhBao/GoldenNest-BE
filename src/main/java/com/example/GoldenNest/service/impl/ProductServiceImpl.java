package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.service.MinioService;
import com.example.GoldenNest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MinioService minioService;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, MinioService minioService) {
        this.productRepository = productRepository;
        this.minioService = minioService;
    }

    // Add product and handle image upload
    @Override
    public Product addProduct(ProductDTO productDTO) throws Exception {
        // Create a new Product entity from the DTO
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setCategory(productDTO.getCategory());

        // Handle image upload if a file is provided
        if (productDTO.getImageFile() != null && !productDTO.getImageFile().isEmpty()) {
            String imageUrl = uploadProductImage(productDTO.getImageFile());
            product.setImageUrl(imageUrl);  // Set the image URL for the product
        }

        // Save the product to the database
        return productRepository.save(product);
    }

    // Upload the image file to MinIO and return the image URL
    private String uploadProductImage(MultipartFile imageFile) throws Exception {
        // Ensure that the bucket exists in MinIO
        String bucketName = "products";
        minioService.ensureBucketExists(bucketName);

        // Generate a unique object name for the image
        String objectName = "product/" + System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
        String contentType = minioService.getContentType(imageFile.getOriginalFilename());

        // Upload the image file to MinIO
        try (InputStream inputStream = imageFile.getInputStream()) {
            minioService.uploadFile(bucketName, objectName, inputStream, inputStream.available(), contentType);
        }

        // Return the URL of the uploaded image
        return bucketName + "/" + objectName;
    }
}
