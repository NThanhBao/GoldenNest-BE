package com.example.GoldenNest.controller;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.service.ProductMediaService;
import com.example.GoldenNest.service.ProductService;
import com.example.GoldenNest.util.annotation.CheckAdmin;
import com.example.GoldenNest.util.annotation.CheckLogin;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import static com.example.GoldenNest.service.impl.AuthServiceImpl.logger;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductMediaService productMediaService;

    public ProductController(ProductService productService, ProductMediaService productMediaService) {
        this.productService = productService;
        this.productMediaService = productMediaService;
    }

    @CheckLogin
    @CheckAdmin
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

    @GetMapping
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getAllProducts(pageable);
    }

    @CheckLogin
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductDTO productDTO) {
        Product newProduct = productService.createPosts(productDTO);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @CheckLogin
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDTO);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CheckLogin
    @CheckAdmin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/categories/{categoryId}")
    public Page<Product> getProductsByCategoryId(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.getProductsByCategoryId(categoryId, pageable);
    }

    @GetMapping("/search")
    public Page<Product> searchProducts(@RequestParam("name") String name,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.searchProductsByName(name, pageable);
    }
}
