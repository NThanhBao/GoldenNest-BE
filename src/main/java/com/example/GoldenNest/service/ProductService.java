package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.ProductDTO;
import com.example.GoldenNest.model.entity.Product;

import java.io.IOException;

public interface ProductService {

    Product addProduct(ProductDTO productDTO) throws IOException, Exception;

}
