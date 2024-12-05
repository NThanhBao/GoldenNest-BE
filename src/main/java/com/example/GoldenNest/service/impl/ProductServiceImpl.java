package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;
import com.example.GoldenNest.repositories.ProductRepository;
import com.example.GoldenNest.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {


    @Override
    public Product addProduct(ProductDTO productDTO) {
        return null;
    }
}
