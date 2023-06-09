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
    public ResponseEntity<CollectionModel<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAll();

        for (Category category : categories) {
            category.add(
                    linkTo(methodOn(EventController.class).getEvent(category.getId())).withRel(GET_ALL_CATEGORIES_REL)
            );
        }

        CollectionModel<Category> eventCollectionModel = CollectionModel.of(categories);
        eventCollectionModel.add(
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).createCategory(new Category())).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(eventCollectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategory(@PathVariable Long id) {
        Category category = categoryService.get(id);

        category.add(
                linkTo(methodOn(CategoryController.class).getCategory(id)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).createCategory(category)).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(category);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<CategoryWithEventsDto> getCategoryEvents(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean upcoming
    ) {
        Category category = categoryService.getWithEvents(id, page, size, upcoming);

        category.add(
                linkTo(methodOn(CategoryController.class).getCategory(id)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).createCategory(category)).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(new CategoryWithEventsDto(category));
    }


    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.create(category);

        createdCategory.add(
                linkTo(methodOn(CategoryController.class).createCategory(category)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).getCategory(createdCategory.getId())).withRel(GET_CATEGORY_REL)
        );

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCategory.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdCategory);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDto categoryDto) {
        Category updatedCategory = categoryService.update(id, categoryDto);

        updatedCategory.add(
                linkTo(methodOn(CategoryController.class).updateCategory(id, categoryDto)).withRel(SELF_REL),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel(GET_ALL_CATEGORIES_REL),
                linkTo(methodOn(CategoryController.class).createCategory(updatedCategory)).withRel(CREATE_CATEGORY_REL)
        );

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.ok().build();
    }
}
