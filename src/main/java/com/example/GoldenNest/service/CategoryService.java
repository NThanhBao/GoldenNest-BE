package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.CategoryDTO;
import com.example.GoldenNest.model.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    Page<Category> getAllCategories(Pageable pageable);

    Category addCategory(CategoryDTO categoryDTO);

    Category updateCategory(String id, CategoryDTO categoryDTO);

    void deleteCategory(String id);

}
