package com.eventsphere.event.service;

import com.eventsphere.event.exception.AlreadyExistsException;
import com.eventsphere.event.exception.CategoryHasEventsException;
import com.eventsphere.event.exception.CategoryNotFoundException;
import com.eventsphere.event.exception.CategoryNotValidException;
import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import com.eventsphere.event.model.dto.CategoryDto;
import com.eventsphere.event.repository.CategoryRepository;
import com.eventsphere.event.repository.EventRepository;
import com.eventsphere.event.util.RabbitMqSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private final RabbitMqSender sender;

    public List<Category> getAll(final int page, final int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryRepository.findAll(pageable).getContent();
    }

    public Category get(final Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    public Category getWithEvents(final Long id, final int page, final int size, final boolean upcoming) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
        Pageable pageable = PageRequest.of(page, size);

        Page<Event> eventsPage = upcoming ? eventRepository.findUpcomingEventsByCategory(category, pageable) :
                eventRepository.findByCategory(category, pageable);

        category.setEvents(new HashSet<>(eventsPage.getContent()));

        return category;
    }


    public Category get(final String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new CategoryNotFoundException(name));
    }

    public Category save(final Category category) {
        try {
            category.setCreatedAt(null);
            category.setUpdatedAt(null);

            return categoryRepository.save(category);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CategoryNotValidException("Invalid category data: " + ex.getMessage());
        }
    }

    public Category create(final Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new AlreadyExistsException("This category is already registered");
        } else {
            return save(category);
        }
    }

    public Category update(final Long categoryId, final CategoryDto categoryDto) {
        Category categoryFromDb = get(categoryId);

        if (categoryDto.getName() != null &&
                checkNameUpdate(categoryFromDb.getName(), categoryDto.getName())) {
            categoryFromDb.setName(categoryDto.getName());
        }

        return save(categoryFromDb);
    }

    public boolean checkNameUpdate(final String nameFromDb, final String updatedName) {
        if (!updatedName.equals(nameFromDb) && categoryRepository.existsByName(updatedName)) {
            throw new AlreadyExistsException("This name is already registered");
        }
        return true;
    }

    public void delete(final Long id) {
        if (categoryRepository.existsById(id)) {
            Category category = get(id);

            if (eventRepository.existsByCategory(category)) {
                throw new CategoryHasEventsException(
                        String.format("Category %s with id %d has events", category.getName(), category.getId())
                );
            }

            log.info("Sending deleted event id {} message to user-service", id);
            sender.sendDeletedCategoryId(id);
            categoryRepository.deleteById(id);
        } else {
            throw new CategoryNotFoundException(id);
        }
    }
}