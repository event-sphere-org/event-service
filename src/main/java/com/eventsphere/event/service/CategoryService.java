package com.eventsphere.event.service;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.CategoryNotFoundException;
import com.eventsphere.event.exception.CategoryNotValidException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.CategoryDto;
import com.eventsphere.event.repository.CategoryRepository;
import com.eventsphere.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category get(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category getWithEvents(Long id, int page, int size, boolean upcoming) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = upcoming ? eventRepository.findUpcomingEventsByCategory(category, pageable) :
                eventRepository.findByCategory(category, pageable);

        category.setEvents(new HashSet<>(eventsPage.getContent()));

        return category;
    }


    public Category get(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
    }

    public Category save(Category category) {
        try {
            category.setCreatedAt(null);
            category.setUpdatedAt(null);

            return categoryRepository.save(category);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CategoryNotValidException("Invalid category data: " + ex.getMessage());
        }
    }

    public Category create(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new AlreadyExistsException("This category is already registered");
        } else {
            return save(category);
        }
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