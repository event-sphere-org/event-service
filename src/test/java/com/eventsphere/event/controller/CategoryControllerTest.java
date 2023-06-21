package com.eventsphere.event.controller;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.dto.CategoryDto;
import com.eventsphere.event.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void getAllCategoriesReturnsCategoriesWithLinks() throws Exception {
        // Given
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category 1");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category 2");

        List<Category> categories = Arrays.asList(category1, category2);

        when(categoryService.getAll()).thenReturn(categories);

        // When/Then
        mockMvc.perform(get("/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/categories"))
                .andExpect(jsonPath("$._links.create-category.href").value("http://localhost/v1/categories"))
                .andExpect(jsonPath("$._embedded.categoryList").isArray())
                .andExpect(jsonPath("$._embedded.categoryList.length()").value(categories.size()))
                .andExpect(jsonPath("$._embedded.categoryList[0].id").value(category1.getId()))
                .andExpect(jsonPath("$._embedded.categoryList[0].name").value(category1.getName()))
                .andExpect(jsonPath("$._embedded.categoryList[0]._links.get-category.href").value("http://localhost/v1/categories/1"))
                .andExpect(jsonPath("$._embedded.categoryList[1].id").value(category2.getId()))
                .andExpect(jsonPath("$._embedded.categoryList[1].name").value(category2.getName()))
                .andExpect(jsonPath("$._embedded.categoryList[1]._links.get-category.href").value("http://localhost/v1/categories/2"));

        verify(categoryService).getAll();
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void getCategoryValidIdReturnsCategoryWithLinks() throws Exception {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");

        when(categoryService.get(1L)).thenReturn(category);

        // When/Then
        mockMvc.perform(get("/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/categories/1"))
                .andExpect(jsonPath("$._links.get-all-categories.href").value("http://localhost/v1/categories"))
                .andExpect(jsonPath("$._links.create-category.href").value("http://localhost/v1/categories"));

        verify(categoryService).get(1L);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void getCategoryEventsValidIdReturnsCategoryWithEventsAndLinks() throws Exception {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");

        when(categoryService.getWithEvents(1L, 0, 10, false)).thenReturn(category);

        // When/Then
        mockMvc.perform(get("/v1/categories/1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));

        verify(categoryService).getWithEvents(1L, 0, 10, false);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void createCategoryValidCategoryReturnsCreatedCategoryWithLocationHeader() throws Exception {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");

        when(categoryService.create(any(Category.class))).thenReturn(category);

        // When/Then
        mockMvc.perform(post("/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Category 1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(header().string("Location", "http://localhost/v1/categories/1"));

        verify(categoryService).create(any(Category.class));
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void updateCategoryValidIdAndDtoReturnsUpdatedCategory() throws Exception {
        // Given
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category 1");

        when(categoryService.update(1L, categoryDto)).thenReturn(category);

        // When/Then
        mockMvc.perform(patch("/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Category 1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost/v1/categories/1"))
                .andExpect(jsonPath("$._links.get-all-categories.href").value("http://localhost/v1/categories"))
                .andExpect(jsonPath("$._links.create-category.href").value("http://localhost/v1/categories"));

        verify(categoryService).update(1L, categoryDto);
        verifyNoMoreInteractions(categoryService);
    }

    @Test
    void deleteCategoryValidIdReturnsOkStatus() throws Exception {
        // When/Then
        mockMvc.perform(delete("/v1/categories/1"))
                .andExpect(status().isOk());

        verify(categoryService).delete(1L);
        verifyNoMoreInteractions(categoryService);
    }
}
