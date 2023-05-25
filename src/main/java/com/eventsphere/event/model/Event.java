package com.eventsphere.event.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Event {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Long id;
    @Basic
    @Column(name = "creator_id")
    private Long creatorId;
    @Basic
    @Column(name = "title")
    private String title;
    @Basic
    @Column(name = "description")
    private String description;
    @Basic
    @Column(name = "image_url")
    private String imageUrl;
    @Basic
    @Column(name = "location")
    private String location;
    @Basic
    @Column(name = "date")
    private Date date;
    @Basic
    @Column(name = "time")
    private Time time;
    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category categoryByCategoryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (id != null ? !id.equals(event.id) : event.id != null) return false;
        if (creatorId != null ? !creatorId.equals(event.creatorId) : event.creatorId != null) return false;
        if (title != null ? !title.equals(event.title) : event.title != null) return false;
        if (description != null ? !description.equals(event.description) : event.description != null) return false;
        if (imageUrl != null ? !imageUrl.equals(event.imageUrl) : event.imageUrl != null) return false;
        if (location != null ? !location.equals(event.location) : event.location != null) return false;
        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        if (time != null ? !time.equals(event.time) : event.time != null) return false;
        if (createdAt != null ? !createdAt.equals(event.createdAt) : event.createdAt != null) return false;
        if (updatedAt != null ? !updatedAt.equals(event.updatedAt) : event.updatedAt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        return result;
    }

    public Category getCategoryByCategoryId() {
        return categoryByCategoryId;
    }

    public void setCategoryByCategoryId(Category categoryByCategoryId) {
        this.categoryByCategoryId = categoryByCategoryId;
    }
}
