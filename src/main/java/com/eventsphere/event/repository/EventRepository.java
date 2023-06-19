package com.eventsphere.event.repository;

import com.eventsphere.event.model.Category;
import com.eventsphere.event.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Check if an event with the given title exists.
     *
     * @param title the title to check
     * @return true if an event with the title exists, false otherwise
     */
    boolean existsByTitle(String title);

    Page<Event> findByCategory(Category category, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.category = :category AND e.date >= CURRENT_TIMESTAMP ORDER BY e.date ASC")
    Page<Event> findUpcomingEventsByCategory(@Param("category") Category category, Pageable pageable);

    void deleteAllByCreatorId(Long creatorId);

    Page<Event> findByCreatorId(Long creatorId, Pageable pageable);
}
