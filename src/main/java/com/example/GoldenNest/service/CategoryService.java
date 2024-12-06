package com.example.GoldenNest.service;

import com.example.GoldenNest.model.dto.CategoryDTO;
import com.example.GoldenNest.model.entity.Category;

public interface CategoryService {

    Category addCategory(CategoryDTO categoryDTO);

    Category updateCategory(String id, CategoryDTO categoryDTO);

    void deleteCategory(String id);

}
