package com.eventsphere.event.controller;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.dto.CategoryDto;
import com.eventsphere.event.model.dto.CategoryWithEventsDto;
import com.eventsphere.event.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private static final String GET_CATEGORY_REL = "get-category";
    private static final String CREATE_CATEGORY_REL = "create-category";
    private static final String GET_ALL_CATEGORIES_REL = "get-all-categories";
    private static final String SELF_REL = "self";
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<CollectionModel<Category>> getAllCategories(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size
    ) {
        List<Category> categories = categoryService.getAll(page, size);

        for (Category category : categories) {
            category.add(
                    linkTo(methodOn(CategoryController.class).getCategory(category.getId())).withRel(GET_CATEGORY_REL)
            );
        }

        CollectionModel<Category> categoryCollectionModel = CollectionModel.of(categories);
        categoryCollectionModel.add(
                linkTo(methodOn(CategoryController.class).getAllCategories(0, 10)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).createCategory(new Category())).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(categoryCollectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable final Long id) {
        Category category = categoryService.get(id);

        category.add(
                linkTo(methodOn(CategoryController.class).getCategory(id)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories(0, 10)).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).createCategory(category)).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<CategoryWithEventsDto> getCategoryEvents(
            @PathVariable final Long id,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(defaultValue = "false") final boolean upcoming
    ) {
        Category category = categoryService.getWithEvents(id, page, size, upcoming);

        category.add(
                linkTo(methodOn(CategoryController.class).getCategory(id)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories(0, 10)).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).createCategory(category)).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(new CategoryWithEventsDto(category));
    }


    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody final Category category) {
        Category createdCategory = categoryService.create(category);

        createdCategory.add(
                linkTo(methodOn(CategoryController.class).createCategory(category)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories(0, 10)).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).getCategory(createdCategory.getId())).withRel(GET_CATEGORY_REL)
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdCategory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable final Long id,
            @Valid @RequestBody final CategoryDto categoryDto
    ) {
        Category updatedCategory = categoryService.update(id, categoryDto);

        updatedCategory.add(
                linkTo(methodOn(CategoryController.class).updateCategory(id, categoryDto)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories(0, 10)).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).createCategory(updatedCategory)).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable final Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
