package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.model.entity.ProductMedia;
import com.example.GoldenNest.service.ProductMediaService;
import com.example.GoldenNest.service.ProductService;
import com.example.GoldenNest.util.annotation.CheckLogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.example.GoldenNest.service.impl.AuthServiceImpl.logger;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMediaService productMediaService;

    @CheckLogin
    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadMedia(@RequestParam("filePath") MultipartFile filePath) {
        if (filePath.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            String mediaId = productMediaService.uploadMedia(filePath);
            return ResponseEntity.ok(mediaId);
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file: " + e.getMessage());
        }
    }

    @CheckLogin
    @PostMapping("/create")
    public ResponseEntity<Product> createPosts(@RequestBody ProductDTO productDTO) {
        Product newPost = productService.createPosts(productDTO);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }

}
