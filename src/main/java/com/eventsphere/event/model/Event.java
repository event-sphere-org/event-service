package com.eventsphere.event.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Event extends RepresentationModel<Event> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;

    @Basic
    @Column(name = "creator_id")
    private Long creatorId;

    @Basic
    @Column(name = "title", nullable = false)
    @Size(min = 3, message = "Title must be at least 3 characters")
    @Size(max = 50, message = "Title must be no more than 50 characters")
    private String title;

    @Basic
    @Column(name = "description")
    @Size(max = 300, message = "Description must be no more than 300 characters")
    private String description;

    @Basic
    @Column(name = "image_url")
    private String imageUrl;

    @Basic
    @Column(name = "location",nullable = false)
    @Size(min = 3, message = "Location must be at least 3 characters")
    private String location;

    @Basic
    @Column(name = "date", nullable = false)
    @Future(message = "Date can't be in the past =) ")
    private Date date;

    @Basic
    @Column(name = "time", nullable = false)
    @NotNull
    private Time time;

    @Basic
    @Column(name = "created_at")
    @CreationTimestamp
    @Null(message = "Cannot manually set creation date")
    private Timestamp createdAt;

    @Basic
    @Column(name = "updated_at")
    @UpdateTimestamp
    @Null(message = "Cannot manually set updated at date")
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @NotNull
    private Category category;

    public Event(Long id, Long creatorId, String title, String description, String imageUrl, String location, Date date, @NotNull Time time, Category category) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    public Event(Long creatorId, String title, String description, String imageUrl, String location, Date date, @NotNull Time time, @NotNull Category category) {
        this.creatorId = creatorId;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.location = location;
        this.date = date;
        this.time = time;
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Event event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
