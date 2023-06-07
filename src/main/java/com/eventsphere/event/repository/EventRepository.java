package com.eventsphere.event.repository;

import com.eventsphere.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Check if an event with the given title exists.
     *
     * @param title the title to check
     * @return true if a user with the title exists, false otherwise
     */
    boolean existsByTitle(String title);
}
