package com.eventsphere.event.service;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.CategoryNotFoundException;
import com.eventsphere.event.exception.CategoryNotValidException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.dto.CategoryDto;
import com.eventsphere.event.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category get(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category save(Category category) {
        Category savedCategory;

        try {
            category.setCreatedAt(null);
            category.setUpdatedAt(null);

            savedCategory = categoryRepository.save(category);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CategoryNotValidException("Invalid category data: " + ex.getMessage());
        }

        return savedCategory;
    }

    public Category create(Category category) {
        Category createdCategory;

        if (categoryRepository.existsByName(category.getName())) {
            throw new AlreadyExistsException("This category is already registered");
        } else {
            createdCategory = save(category);
        }

        return createdCategory;
    }


    public Category update(Long categoryId, CategoryDto categoryDto) {
        Category categoryFromDb = get(categoryId);

        if (categoryDto.getName() != null &&
                checkNameUpdate(categoryFromDb.getName(), categoryDto.getName())) {
            categoryFromDb.setName(categoryDto.getName());
        }
        return save(categoryFromDb);
    }

    public boolean checkNameUpdate(String nameFromDb, String updatedName) {
        if (!updatedName.equals(nameFromDb) && categoryRepository.existsByName(updatedName)) {
            throw new AlreadyExistsException("This name is already registered");
        }
        return true;
    }

    public void delete(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new CategoryNotFoundException(id);
        }
    }
}