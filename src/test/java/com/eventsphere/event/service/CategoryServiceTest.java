package com.eventsphere.event.service;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.CategoryNotFoundException;
import com.eventsphere.event.exception.CategoryNotValidException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.CategoryDto;
import com.eventsphere.event.repository.CategoryRepository;
import com.eventsphere.event.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository, eventRepository);
    }

    @Test
    void getAllCategories() {
        // Given
        List<Category> expectedCategories = new ArrayList<>();
        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // When
        List<Category> actualCategories = categoryService.getAll();

        // Then
        assertSame(expectedCategories, actualCategories);
        verify(categoryRepository).findAll();
    }

    @Test
    void getExistingCategoryById() {
        // Given
        Long categoryId = 1L;
        Category expectedCategory = new Category();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));

        // When
        Category actualCategory = categoryService.get(categoryId);

        // Then
        assertSame(expectedCategory, actualCategory);
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getNonExistingCategoryById() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.get(categoryId));
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void getExistingCategoryWithEvents() {
        // Given
        Long categoryId = 1L;
        int page = 0;
        int size = 10;
        boolean upcoming = true;
        Category expectedCategory = new Category();
        Page<Event> eventsPage = new PageImpl<>(new ArrayList<>());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(expectedCategory));
        when(eventRepository.findUpcomingEventsByCategory(expectedCategory, PageRequest.of(page, size)))
                .thenReturn(eventsPage);

        // When
        Category actualCategory = categoryService.getWithEvents(categoryId, page, size, upcoming);

        // Then
        assertSame(expectedCategory, actualCategory);
        assertEquals(new HashSet<>(eventsPage.getContent()), actualCategory.getEvents());
        verify(categoryRepository).findById(categoryId);
        verify(eventRepository).findUpcomingEventsByCategory(expectedCategory, PageRequest.of(page, size));
    }

    @Test
    void getNonExistingCategoryWithEvents() {
        // Given
        Long categoryId = 1L;
        int page = 0;
        int size = 10;
        boolean upcoming = true;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.getWithEvents(categoryId, page, size, upcoming));
        verify(categoryRepository).findById(categoryId);
        verifyNoInteractions(eventRepository);
    }

    @Test
    void getExistingCategoryByName() {
        // Given
        String categoryName = "Test Category";
        Category expectedCategory = new Category();
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(expectedCategory));

        // When
        Category actualCategory = categoryService.get(categoryName);

        // Then
        assertSame(expectedCategory, actualCategory);
        verify(categoryRepository).findByName(categoryName);
    }

    @Test
    void getNonExistingCategoryByName() {
        // Given
        String categoryName = "Test Category";
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.get(categoryName));
        verify(categoryRepository).findByName(categoryName);
    }

    @Test
    void saveValidCategory() {
        // Given
        Category category = new Category();

        // When
        Category savedCategory = new Category();
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        Category result = categoryService.save(category);

        // Then
        assertSame(savedCategory, result);
        verify(categoryRepository).save(category);
    }

    @Test
    void saveInvalidCategory() {
        // Given
        Category category = new Category();
        doThrow(RuntimeException.class).when(categoryRepository).save(category);

        // When and Then
        assertThrows(CategoryNotValidException.class, () -> categoryService.save(category));
        verify(categoryRepository).save(category);
    }

    @Test
    void createNewCategory() {
        // Given
        Category category = new Category();
        String categoryName = "New Category";
        category.setName(categoryName);
        when(categoryRepository.existsByName(categoryName)).thenReturn(false);

        // When
        Category savedCategory = new Category();
        when(categoryRepository.save(category)).thenReturn(savedCategory);
        Category result = categoryService.create(category);

        // Then
        assertSame(savedCategory, result);
        verify(categoryRepository).existsByName(categoryName);
        verify(categoryRepository).save(category);
    }

    @Test
    void createExistingCategory() {
        // Given
        Category category = new Category();
        String categoryName = "Existing Category";
        category.setName(categoryName);
        when(categoryRepository.existsByName(categoryName)).thenReturn(true);

        // When and Then
        assertThrows(AlreadyExistsException.class, () -> categoryService.create(category));
        verify(categoryRepository).existsByName(categoryName);
    }

    @Test
    void updateCategory() {
        // Given
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category");
        Category existingCategory = new Category();
        existingCategory.setName("Old Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(false);

        // When
        Category savedCategory = new Category();
        when(categoryRepository.save(existingCategory)).thenReturn(savedCategory);
        Category result = categoryService.update(categoryId, categoryDto);

        // Then
        assertSame(savedCategory, result);
        assertEquals(categoryDto.getName(), existingCategory.getName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName(categoryDto.getName());
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    void updateCategoryWithNameAlreadyExists() {
        // Given
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Existing Category");
        Category existingCategory = new Category();
        existingCategory.setName("Old Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByName(categoryDto.getName())).thenReturn(true);

        // When and Then
        assertThrows(AlreadyExistsException.class, () -> categoryService.update(categoryId, categoryDto));
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).existsByName(categoryDto.getName());
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void updateCategoryWithSameName() {
        // Given
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Old Category");
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        // When
        Category savedCategory = new Category();
        when(categoryRepository.save(existingCategory)).thenReturn(savedCategory);
        Category result = categoryService.update(categoryId, categoryDto);

        // Then
        assertSame(savedCategory, result);
        assertEquals(categoryDto.getName(), existingCategory.getName());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(existingCategory);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void deleteExistingCategory() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        // When
        categoryService.delete(categoryId);

        // Then
        verify(categoryRepository).existsById(categoryId);
        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    void deleteNonExistingCategory() {
        // Given
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        // When and Then
        assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(categoryId));
        verify(categoryRepository).existsById(categoryId);
        verifyNoMoreInteractions(categoryRepository);
    }
}
