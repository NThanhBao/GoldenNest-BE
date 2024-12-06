package com.example.GoldenNest.service.impl;

import com.example.GoldenNest.model.dto.CategoryDTO;
import com.example.GoldenNest.model.entity.Category;
import com.example.GoldenNest.repositories.CategoryRepository;
import com.example.GoldenNest.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new IllegalArgumentException("Category with this name already exists");
        }

        Category category = new Category();
        category.setId(UUID.randomUUID().toString());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(String id, CategoryDTO categoryDTO) {
        Optional<Category> existingCategoryOpt = categoryRepository.findById(id);
        if (!existingCategoryOpt.isPresent()) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        Category existingCategory = existingCategoryOpt.get();

        // Update the category information
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        existingCategory.setUpdatedAt(LocalDateTime.now());

        return categoryRepository.save(existingCategory);
    }

    @Override
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found with id: " + id);
        }

        categoryRepository.deleteById(id);
    }
}
